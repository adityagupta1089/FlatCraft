package world.multi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.EditText;

import com.badlogic.gdx.math.Vector2;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.multiplayer.adt.message.IMessage;
import org.andengine.extension.multiplayer.adt.message.Message;
import org.andengine.extension.multiplayer.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.adt.message.server.IServerMessage;
import org.andengine.extension.multiplayer.client.IServerMessageHandler;
import org.andengine.extension.multiplayer.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.client.connector.SocketConnectionServerConnector;
import org.andengine.extension.multiplayer.server.IClientMessageHandler;
import org.andengine.extension.multiplayer.server.SocketServer;
import org.andengine.extension.multiplayer.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.shared.SocketConnection;
import org.andengine.extension.multiplayer.util.MessagePool;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.adt.color.Color;
import org.andengine.util.debug.Debug;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import hud.InventoryItem;
import manager.ResourcesManager;
import manager.SceneManager;
import object.player.CreativePlayer;
import object.tile.Tile;
import spritesheet.TileSpritesheet;
import world.World;
import world.constants.CreativeConstants;

public class MultiPlayerWorld extends World implements CreativeConstants,
        ClientMessageFlags, ServerMessageFlags {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOCALHOST_IP = "127.0.0.1";

    private static final int SERVER_PORT = 4444;

    private static final short FLAG_MESSAGE_ADD_SPRITE = 1;
    private static final short FLAG_MESSAGE_DELETE_SPRITE = FLAG_MESSAGE_ADD_SPRITE + 1;

    private static final int DIALOG_CHOOSE_SERVER_OR_CLIENT_ID = 0;
    private static final int DIALOG_ENTER_SERVER_IP_ID = DIALOG_CHOOSE_SERVER_OR_CLIENT_ID + 1;
    private static final int DIALOG_SHOW_SERVER_IP_ID = DIALOG_ENTER_SERVER_IP_ID + 1;

    // ===========================================================
    // Fields
    // ===========================================================

    private String mServerIP = LOCALHOST_IP;
    private SocketServer<SocketConnectionClientConnector> mSocketServer;
    private ServerConnector<SocketConnection> mServerConnector;

    private final MessagePool<IMessage> mMessagePool = new MessagePool<>();

    // ===========================================================
    // Constructor
    // ===========================================================
    public MultiPlayerWorld(BoundCamera camera) {
        super(camera);
        camera.setBounds(0, 0, GRID_WIDTH * Tile.TILE_EDGE, GRID_HEIGHT * Tile.TILE_EDGE);
        camera.setBoundsEnabled(true);
        player.setLinearDamping(PLAYER_DAMPING);
        initMessagePool();
        showDialog(DIALOG_CHOOSE_SERVER_OR_CLIENT_ID);
    }

    private void initMessagePool() {
        mMessagePool.registerMessage(FLAG_MESSAGE_ADD_SPRITE, AddSpriteMessage.class);
        mMessagePool.registerMessage(FLAG_MESSAGE_DELETE_SPRITE, DeleteSpriteMessage
                .class);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
    @Override
    public void createPlayer(Camera camera) {
        player = new CreativePlayer(GRID_WIDTH / 2 * Tile.TILE_EDGE, (DIRT_WIDTH + 1) * Tile
                .TILE_EDGE, physicsWorld);
        player.setPosition(player.getX() + player.getWidth() / 2, player.getY() + player
                .getHeight() / 2);
        camera.setCenter(player.getX(), player.getY());
        camera.setChaseEntity(player);
        this.attachChild(player);
        entities.add(player);
    }

    @Override
    public Vector2 getGravity() {
        return new Vector2(GRAVITY_X, GRAVITY_Y);
    }

    @Override
    public void createBackground() {
        setBackground(new Background(Color.WHITE));
        int separationLayer = BACKGROUND_GRID_HEIGHT / 2;
        for (int i = 0; i < BACKGROUND_GRID_HEIGHT + 1; i++) {
            for (int j = 0; j < BACKGROUND_GRID_WIDTH + 1; j++) {
                TextureRegion temp;
                if (i < separationLayer) {
                    temp = ResourcesManager.skyBoxBottomRegion;
                } else if (i > separationLayer) {
                    temp = ResourcesManager.skyBoxTopRegion;
                } else {
                    temp = ResourcesManager.skyBoxSideHillsRegion;
                }
                Sprite bgtile = new Sprite(j * BACKGROUND_TILE_EDGE + BACKGROUND_TILE_EDGE / 2, i
                        * BACKGROUND_TILE_EDGE + BACKGROUND_TILE_EDGE / 2, BACKGROUND_TILE_EDGE,
                        BACKGROUND_TILE_EDGE, temp, ResourcesManager.vertexBufferObjectManager);
                attachChild(bgtile);
                entities.add(bgtile);
            }
        }
    }

    @Override
    public void createForeground() {
        int i = 0;
        /* Layers of dirt */
        for (; i < DIRT_WIDTH; i++) {
            for (int j = 0; j < GRID_WIDTH; j++) {
                createTile(j, i, TileSpritesheet.DIRT_ID);

            }
        }
        /* One layer of grass */
        for (int j = 0; j < GRID_WIDTH; j++) {
            createTile(j, i, TileSpritesheet.DIRT_GRASS_ID);
        }

		/* Border */
        for (i = 0; i < GRID_WIDTH; i++) {
            createTile(i, -1, TileSpritesheet.DIRT_ID);
            createTile(i, GRID_HEIGHT, TileSpritesheet.DIRT_ID);
        }
        for (i = 0; i < GRID_HEIGHT; i++) {
            createTile(-1, i, TileSpritesheet.DIRT_ID);
            createTile(GRID_WIDTH, i, TileSpritesheet.DIRT_ID);
        }
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        if (pSceneTouchEvent.isActionUp()) {
            int blockX = ((int) pSceneTouchEvent.getX()) / Tile.TILE_EDGE;
            int blockY = ((int) pSceneTouchEvent.getY()) / Tile.TILE_EDGE;
            if (blockX != ((int) player.getX()) / Tile.TILE_EDGE || blockY != ((int) player.getY
                    ()) / Tile.TILE_EDGE) {
                if (placeMode == MODE_DELETE_TILES && grid.indexOfKey(position(blockX, blockY)) >
                        0) {
                    ResourcesManager.hud.currItem.give();
                    deleteTile(blockX, blockY);
                    final DeleteSpriteMessage deleteSpriteMessage =
                            (DeleteSpriteMessage) mMessagePool
                                    .obtainMessage(FLAG_MESSAGE_DELETE_SPRITE);
                    deleteSpriteMessage.set(blockX, blockY);
                    if (mSocketServer != null) {
                        mSocketServer.sendBroadcastServerMessage(ClientConnector.
                                        PRIORITY_DEFAULT,
                                deleteSpriteMessage);
                        mMessagePool.recycleMessage(deleteSpriteMessage);
                    } else {
                        mServerConnector.sendClientMessage(ClientConnector.
                                PRIORITY_DEFAULT, deleteSpriteMessage);
                        mMessagePool.recycleMessage(deleteSpriteMessage);
                    }
                    return true;
                } else if (placeMode == MODE_PLACE_TILES && grid.indexOfKey(position(blockX,
                        blockY)) < 0) {
                    if (ResourcesManager.hud.currItem.take()) {
                        createTile(blockX, blockY, ResourcesManager.hud.currItem.mTileType);
                        final AddSpriteMessage addSpriteMessage =
                                (AddSpriteMessage) mMessagePool
                                        .obtainMessage(FLAG_MESSAGE_ADD_SPRITE);
                        addSpriteMessage.set(blockX, blockY, ResourcesManager.hud.currItem
                                .mTileType);
                        if (mSocketServer != null) {
                            mSocketServer.sendBroadcastServerMessage(ClientConnector.
                                    PRIORITY_DEFAULT, addSpriteMessage);
                            mMessagePool.recycleMessage(addSpriteMessage);
                        } else {
                            mServerConnector.sendClientMessage(ClientConnector.
                                    PRIORITY_DEFAULT, addSpriteMessage);
                            mMessagePool.recycleMessage(addSpriteMessage);
                        }
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void onPopulateQuickAccess(List<InventoryItem> qa) {
        // TODO add more inventory items
        for (int i = TileSpritesheet.MIN_INDEX; i < TileSpritesheet.MAX_INDEX; i++) {
            qa.add(new InventoryItem(i, 100));
        }
    }

    // ===========================================================
    // Methods
    // ===========================================================
    private void showDialog(final int pID) {
        ResourcesManager.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (pID) {
                    case DIALOG_SHOW_SERVER_IP_ID:
                        new AlertDialog.Builder(ResourcesManager.gameActivity)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("Your Server-IP ...")
                                .setCancelable(false)
                                .setMessage("The IP of your Server is:\n" + getLocalIpAddress())
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        break;
                    case DIALOG_ENTER_SERVER_IP_ID:
                        final EditText ipEditText = new EditText(ResourcesManager.gameActivity);
                        new AlertDialog.Builder(ResourcesManager.gameActivity)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("Enter Server-IP ...")
                                .setCancelable(false)
                                .setView(ipEditText)
                                .setPositiveButton("Connect", new DialogInterface.OnClickListener
                                        () {
                                    @Override
                                    public void onClick(final DialogInterface pDialog, final int
                                            pWhich) {
                                        mServerIP = ipEditText.getText().toString();
                                        initClient();
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface
                                        .OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface pDialog, final int
                                            pWhich) {
                                        Log.d("Multi", "Finishing");
                                    }
                                })
                                .show();
                        break;
                    case DIALOG_CHOOSE_SERVER_OR_CLIENT_ID:
                        new AlertDialog.Builder(ResourcesManager.gameActivity)
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setTitle("Be Server or Client ...")
                                .setCancelable(false)
                                .setPositiveButton("Client", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface pDialog, final int
                                            pWhich) {
                                        showDialog(DIALOG_ENTER_SERVER_IP_ID);
                                    }
                                })
                                .setNegativeButton("Server", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface pDialog, final int
                                            pWhich) {
                                        initServer();
                                        showDialog(DIALOG_SHOW_SERVER_IP_ID);
                                    }
                                })
                                .show();
                        break;
                }
            }
        });
    }

    private void initServer() {
        ResourcesManager.gameActivity.toastOnUiThread("Multi : Initializing Server");
        this.mSocketServer = new SocketServer<SocketConnectionClientConnector>(SERVER_PORT, new
                ClientConnectorListener(), new ServerStateListener()) {
            @Override
            protected SocketConnectionClientConnector newClientConnector(final SocketConnection
                                                                                 pSocketConnection) throws IOException {
                SocketConnectionClientConnector mClientConnector = new
                        SocketConnectionClientConnector(pSocketConnection);
                mClientConnector.registerClientMessage(FLAG_MESSAGE_ADD_SPRITE,
                        AddSpriteMessage.class, new IClientMessageHandler<SocketConnection>() {
                            @Override
                            public void onHandleMessage(ClientConnector<SocketConnection>
                                                                pClientConnector,
                                                        IClientMessage pClientMessage) throws
                                    IOException {
                                final AddSpriteMessage addSpriteMessage =
                                        (AddSpriteMessage) pClientMessage;
                                ResourcesManager.gameActivity.toastOnUiThread("Client: Add " +
                                        "sprite: " + addSpriteMessage.mX + ", " +
                                        addSpriteMessage.mY);
                                createTile(addSpriteMessage.mX, addSpriteMessage.mY,
                                        addSpriteMessage
                                                .mType);
                            }
                        });

                mClientConnector.registerClientMessage(FLAG_MESSAGE_DELETE_SPRITE,
                        DeleteSpriteMessage.class, new IClientMessageHandler<SocketConnection>() {
                            @Override
                            public void onHandleMessage(final ClientConnector<SocketConnection>
                                                                pClientConnector, final
                                                        IClientMessage
                                                                pClientMessage) throws IOException {
                                final DeleteSpriteMessage deleteSpriteMessage =
                                        (DeleteSpriteMessage) pClientMessage;
                                ResourcesManager.gameActivity.toastOnUiThread("Client: Delete " +
                                        "sprite: " + deleteSpriteMessage.mX + ", " +
                                        deleteSpriteMessage.mY);
                                deleteTile(deleteSpriteMessage.mX, deleteSpriteMessage.mY);
                            }
                        });
                return mClientConnector;
            }
        };


        this.mSocketServer.start();
    }

    private void initClient() {
        ResourcesManager.gameActivity.toastOnUiThread("Multi : Initializing Client");
        try {
            this.mServerConnector = new SocketConnectionServerConnector(new SocketConnection(new
                    Socket(this.mServerIP, SERVER_PORT)), new ServerConnectorListener());

            this.mServerConnector.registerServerMessage(FLAG_MESSAGE_SERVER_CONNECTION_CLOSE,
                    ConnectionCloseServerMessage.class, new
                            IServerMessageHandler<SocketConnection>() {
                                @Override
                                public void onHandleMessage(final ServerConnector<SocketConnection>
                                                                    pServerConnector, final
                                                            IServerMessage
                                                                    pServerMessage) throws
                                        IOException {
                                    SceneManager.getCurrentScene().onBackKeyPressed();
                                }
                            });

            this.mServerConnector.registerServerMessage(FLAG_MESSAGE_ADD_SPRITE,
                    AddSpriteMessage.class, new IServerMessageHandler<SocketConnection>() {
                        @Override
                        public void onHandleMessage(final ServerConnector<SocketConnection>
                                                            pServerConnector, final IServerMessage
                                                            pServerMessage) throws IOException {
                            final AddSpriteMessage addSpriteMessage =
                                    (AddSpriteMessage) pServerMessage;
                            ResourcesManager.gameActivity.toastOnUiThread("Server: Add " +
                                    "sprite: " + addSpriteMessage.mX + ", " +
                                    addSpriteMessage.mY);
                            createTile(addSpriteMessage.mX, addSpriteMessage.mY, addSpriteMessage
                                    .mType);
                        }
                    });

            this.mServerConnector.registerServerMessage(FLAG_MESSAGE_DELETE_SPRITE,
                    DeleteSpriteMessage.class, new IServerMessageHandler<SocketConnection>() {
                        @Override
                        public void onHandleMessage(final ServerConnector<SocketConnection>
                                                            pServerConnector, final IServerMessage
                                                            pServerMessage) throws IOException {
                            final DeleteSpriteMessage deleteSpriteMessage =
                                    (DeleteSpriteMessage) pServerMessage;
                            ResourcesManager.gameActivity.toastOnUiThread("Server: Delete " +
                                    "sprite: " + deleteSpriteMessage.mX + ", " +
                                    deleteSpriteMessage.mY);
                            deleteTile(deleteSpriteMessage.mX, deleteSpriteMessage.mY);
                        }
                    });

            this.mServerConnector.getConnection().start();
        } catch (final Throwable t) {
            Debug.e(t);
        }
    }

    public static String getLocalIpAddress() {
        WifiManager wifiMgr = (WifiManager) ResourcesManager.gameActivity.getSystemService
                (Context.WIFI_SERVICE);
        if (wifiMgr.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            int ip = wifiInfo.getIpAddress();
            String wifiIpAddress = String.format("%d.%d.%d.%d",
                    (ip & 0xff),
                    (ip >> 8 & 0xff),
                    (ip >> 16 & 0xff),
                    (ip >> 24 & 0xff));

            return wifiIpAddress;
        }

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }

                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void exit() {
        ResourcesManager.gameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSocketServer != null) {
                    mSocketServer.sendBroadcastServerMessage(ClientConnector.PRIORITY_DEFAULT,
                            new ConnectionCloseServerMessage());
                    mSocketServer.terminate();
                }

                if (mServerConnector != null) {
                    mServerConnector.terminate();
                }
            }
        });

    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
    public static class AddSpriteMessage extends Message implements IServerMessage,
            IClientMessage {
        private int mX;
        private int mY;
        private int mType;

        public AddSpriteMessage() {
        }

        public AddSpriteMessage(final int pX, final int pY, final int pType) {
            this.mX = pX;
            this.mY = pY;
            this.mType = pType;
        }

        public void set(final int pX, final int pY, final int pType) {
            this.mX = pX;
            this.mY = pY;
            this.mType = pType;
        }

        @Override
        public short getFlag() {
            return FLAG_MESSAGE_ADD_SPRITE;
        }

        @Override
        protected void onReadTransmissionData(final DataInputStream pDataInputStream) throws
                IOException {
            this.mX = pDataInputStream.readInt();
            this.mY = pDataInputStream.readInt();
            this.mType = pDataInputStream.readInt();
        }

        @Override
        protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws
                IOException {
            pDataOutputStream.writeInt(this.mX);
            pDataOutputStream.writeInt(this.mY);
            pDataOutputStream.writeInt(this.mType);
        }
    }

    public static class DeleteSpriteMessage extends Message implements IServerMessage,
            IClientMessage {
        private int mX;
        private int mY;

        public DeleteSpriteMessage() {
        }

        public DeleteSpriteMessage(final int pX, final int pY) {
            this.mX = pX;
            this.mY = pY;
        }

        public void set(final int pX, final int pY) {
            this.mX = pX;
            this.mY = pY;
        }

        @Override
        public short getFlag() {
            return FLAG_MESSAGE_DELETE_SPRITE;
        }

        @Override
        protected void onReadTransmissionData(final DataInputStream pDataInputStream) throws
                IOException {
            this.mX = pDataInputStream.readInt();
            this.mY = pDataInputStream.readInt();
        }

        @Override
        protected void onWriteTransmissionData(final DataOutputStream pDataOutputStream) throws
                IOException {
            pDataOutputStream.writeInt(this.mX);
            pDataOutputStream.writeInt(this.mY);
        }
    }

    private class ServerConnectorListener implements
            SocketConnectionServerConnector.ISocketConnectionServerConnectorListener {
        @Override
        public void onStarted(final ServerConnector<SocketConnection> pConnector) {
            ResourcesManager.gameActivity.toastOnUiThread("CLIENT: Connected to server.");
        }

        @Override
        public void onTerminated(final ServerConnector<SocketConnection> pConnector) {
            ResourcesManager.gameActivity.toastOnUiThread("CLIENT: Disconnected from Server...");
            SceneManager.getCurrentScene().onBackKeyPressed();
        }
    }

    private class ServerStateListener implements
            SocketServer.ISocketServerListener<SocketConnectionClientConnector> {
        @Override
        public void onStarted(final SocketServer<SocketConnectionClientConnector> pSocketServer) {
            ResourcesManager.gameActivity.toastOnUiThread("SERVER: Started.");
        }

        @Override
        public void onTerminated(final SocketServer<SocketConnectionClientConnector>
                                         pSocketServer) {
            ResourcesManager.gameActivity.toastOnUiThread("SERVER: Terminated.");
        }

        @Override
        public void onException(final SocketServer<SocketConnectionClientConnector>
                                        pSocketServer, final Throwable pThrowable) {
            Debug.e(pThrowable);
            ResourcesManager.gameActivity.toastOnUiThread("SERVER: Exception: " + pThrowable);
        }
    }

    private class ClientConnectorListener implements
            SocketConnectionClientConnector.ISocketConnectionClientConnectorListener {
        @Override
        public void onStarted(final ClientConnector<SocketConnection> pConnector) {
            ResourcesManager.gameActivity.toastOnUiThread("SERVER: Client connected: " +
                    pConnector.getConnection
                            ().getSocket().getInetAddress().getHostAddress());
        }

        @Override
        public void onTerminated(final ClientConnector<SocketConnection> pConnector) {
            ResourcesManager.gameActivity.toastOnUiThread("SERVER: Client disconnected");
        }
    }

}
