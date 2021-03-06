package no.uib.inf112.core.screens.menuscreens;

import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import no.uib.inf112.core.GameGraphics;
import no.uib.inf112.core.multiplayer.Client;
import no.uib.inf112.core.multiplayer.IClient;
import no.uib.inf112.core.multiplayer.Server;

import java.io.IOException;

public class LobbyScreen extends AbstractMenuScreen {

    protected final IClient client;
    private final Server server;
    protected List<String> playerList;

    public LobbyScreen(GameGraphics game, boolean isHost, String ip, int port) throws IOException {
        super(game);
        if (isHost) {
            server = new Server(port, 8);
            GameGraphics.setServer(server);
            client = new Client(ip, port);
            client.setName(GameGraphics.mainPlayerName);
            client.setHost();
            GameGraphics.setClient(client);
        } else {
            server = null;
            client = new Client(ip, port);
            client.setName(GameGraphics.mainPlayerName);
            client.startGame(game);
            GameGraphics.setClient(client);
        }
        playerList = createList(client.getPlayerNames());
    }

    @Override
    public void show() {
        TextButton returnButton = createReturnButton(50);
        returnButton.setPosition(3 * stage.getWidth() / 4 - returnButton.getWidth() - 10, stage.getHeight() / 20);

        stage.addActor(returnButton);
    }

    @Override
    public void render(float v) {
        super.render(v);
        stage.getActors().removeValue(playerList, false);

        playerList = createList(client.getPlayerNames());
        stage.addActor(playerList);
    }
}