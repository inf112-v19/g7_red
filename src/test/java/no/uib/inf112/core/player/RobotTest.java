package no.uib.inf112.core.player;

import no.uib.inf112.core.GameGraphics;
import no.uib.inf112.core.RoboRally;
import no.uib.inf112.core.map.TileType;
import no.uib.inf112.desktop.TestGraphics;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class RobotTest extends TestGraphics {

    private Robot testBot;
    private int roboX;
    private int roboY;
    private Player player;

    public static final int HEIGHT = 20;
    public static final int WIDTH = 20;
    private RoboRally roboRally;


    @Before
    public void setup() {
        roboRally = GameGraphics.getRoboRally();
        roboRally.getPlayerHandler().generatePlayers();
        player = roboRally.getPlayerHandler().mainPlayer();
        testBot = player.getRobot();
        testBot.teleport(HEIGHT / 2, WIDTH / 2);
        testBot.setDirection(Direction.NORTH);

        roboX = testBot.getX();
        roboY = testBot.getY();
        /*
        PowerMockito.mockStatic(RoboRally.class);
        PowerMockito.mockStatic(PlayerHandler.class);

        PlayerHandler ph = mock(PlayerHandler.class);
        when(ph.mainPlayer()).thenReturn(player);

        MapHandler map = Mockito.mock(TiledMapHandler.class);
        when(map.getMapHeight()).thenReturn(RobotTest.HEIGHT);
        when(map.getMapWidth()).thenReturn(RobotTest.WIDTH);
        when(map.isOutsideBoard(Mockito.anyInt(), Mockito.anyInt())).thenCallRealMethod();

        when(GameGraphics.getRoboRally().getPlayerHandler()).thenReturn(ph);
        when(GameGraphics.getRoboRally().getCurrentMap()).thenReturn(map);*/
    }

    @Test
    public void movingOneFacingNorthShouldNotChangeX() {
        player.moveRobot(new MovementCard(Movement.MOVE_1, 10));
        assertEquals(roboX, testBot.getX());
    }

    @Test
    public void movingOneFacingNorthShouldIncrementY() {
        player.moveRobot(new MovementCard(Movement.MOVE_1, 10));
        assertEquals(roboY + 1, testBot.getY());
    }

    @Test
    public void movingThreeFacingNorthShouldIncreaseYWithThree() {
        player.moveRobot(new MovementCard(Movement.MOVE_3, 10));
        assertEquals(roboY + 3, testBot.getY());
    }

    @Test
    public void movingTwoFacingEastShouldNotChangeY() {
        testBot.setDirection(Direction.EAST);
        player.moveRobot(new MovementCard(Movement.MOVE_2, 10));
        assertEquals(roboY, testBot.getY());
    }

    @Test
    public void movingTwoFacingEastShouldIncreaseXWithTwo(){
        testBot.setDirection(Direction.EAST);
        player.moveRobot(new MovementCard(Movement.MOVE_2, 10));
        assertEquals(roboX + 2, testBot.getX());
    }

    @Test
    public void backingUpWhileFacingNorthShouldDecrementY(){
        player.moveRobot(new MovementCard(Movement.BACK_UP, 10));
        assertEquals(roboY - 1, testBot.getY());
    }

    @Test
    public void backingUpShouldNotAffectDirectionOfRobot(){
        Direction facing = testBot.getDirection();
        player.moveRobot(new MovementCard(Movement.BACK_UP, 10));
        assertEquals(facing, testBot.getDirection());
    }

    @Test
    public void movingForwardShouldNotAffectDirectionOfRobot(){
        Direction facing = testBot.getDirection();
        player.moveRobot(new MovementCard(Movement.MOVE_2, 10));
        player.moveRobot(new MovementCard(Movement.MOVE_1, 10));
        player.moveRobot(new MovementCard(Movement.MOVE_3, 10));
        assertEquals(facing, testBot.getDirection());
    }

    @Test
    public void turningLeftWhileFacingNorthShouldResultInWest(){
        testBot.setDirection(Direction.NORTH); //Just in case setup is changed
        player.moveRobot(new MovementCard(Movement.LEFT_TURN, 10));
        assertEquals(Direction.WEST, testBot.getDirection());
    }

    @Test
    public void turningLeftShouldNotChangeXOrY(){
        player.moveRobot(new MovementCard(Movement.LEFT_TURN, 10));
        assertEquals(roboX, testBot.getX());
        assertEquals(roboY, testBot.getY());
    }

    @Test
    public void turningRightTwiceShouldHaveTheSameResultAsAUTurn(){
        Robot testBot2 = new Robot(5, 5, testBot.getDirection());
        player.moveRobot(new MovementCard(Movement.RIGHT_TURN, 10));
        player.moveRobot(new MovementCard(Movement.RIGHT_TURN, 10));
        player.moveRobot(new MovementCard(Movement.U_TURN, 10));
        assertEquals(testBot.getDirection(), testBot2.getDirection());
    }

    @Test
    public void movingRobotInASquareShouldResultInRobotBeingBackAtStartingPosition(){
        Direction facing = testBot.getDirection();
        for (int i = 0; i < 100; i++) {
            if (i % 4 == 0) {
                assertEquals(roboX, testBot.getX());
                assertEquals(roboY, testBot.getY());
                assertEquals(facing, testBot.getDirection());
            } else {
                assertFalse(roboX == testBot.getX() && roboY == testBot.getY());
                assertNotEquals(facing, testBot.getDirection());
            }
            player.moveRobot(new MovementCard(Movement.MOVE_2, 10));
            player.moveRobot(new MovementCard(Movement.LEFT_TURN, 10));
        }
    }

    @Test
    public void getTileType() {
        for (Direction dir : Direction.values()) {
            testBot.setDirection(dir);
            assertNotNull(testBot.getTileType());

            assertEquals(TileType.Group.ROBOT, testBot.getTileType().getGroup());

            String[] name = testBot.getTileType().name().split("_");
            assertEquals(dir.name(), name[name.length - 1]);
        }
    }

    @Test
    public void movingOutOfBoundTeleportToBackup() {
        player.setBackup(testBot.getX(), testBot.getY());
        testBot.teleport(0, 0);
        testBot.setDirection(Direction.SOUTH);
        player.moveRobot(new MovementCard(Movement.MOVE_1, 10));

        assertEquals(roboX, testBot.getX());
        assertEquals(roboY, testBot.getY());
    }

    @Test
    public void movingOutOfBoundReduceLifeByOne() {
        player.getLives();
        testBot.teleport(0, 0);
        player.damage(1);

        testBot.setDirection(Direction.SOUTH);
        player.moveRobot(new MovementCard(Movement.MOVE_1, 10));

        assertTrue(testBot.shouldUpdate());

        assertEquals(Player.MAX_HEALTH, player.getHealth());
        assertEquals(Player.MAX_LIVES - 1, player.getLives());

    }
}
