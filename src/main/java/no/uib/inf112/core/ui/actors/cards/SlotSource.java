package no.uib.inf112.core.ui.actors.cards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import no.uib.inf112.core.map.cards.Card;
import no.uib.inf112.core.screens.GameScreen;

/**
 * @author Elg
 */
public class SlotSource extends DragAndDrop.Source {

    private final CardSlot sourceSlot;

    SlotSource(final CardSlot actor) {
        super(actor);
        sourceSlot = actor;
    }

    @Override
    public Payload dragStart(final InputEvent event, final float x, final float y, final int pointer) {
        if (sourceSlot == null || sourceSlot.getCard() == null || !GameScreen.getUiHandler().isDrawnCardsVisible() || sourceSlot.isDisabled()) {
            return null;
        }
        GameScreen.getUiHandler().getDad().setDragActorPosition(sourceSlot.getCard().getRegionTexture().getRegionWidth() - x, -y);

        final CardSlot dragActor = sourceSlot.copy();
        sourceSlot.setCard(null);
        sourceSlot.updateCard();


        final Payload payload = new Payload();
        payload.setObject(sourceSlot);
        payload.setDragActor(dragActor);
        payload.setValidDragActor(dragActor);
        payload.setInvalidDragActor(dragActor);

        return payload;
    }

    @Override
    public void dragStop(final InputEvent event, final float x, final float y, final int pointer, final Payload payload,
                         final DragAndDrop.Target target) {
        final CardSlot payloadSlot = (CardSlot) payload.getDragActor();
        CardSlot source = (CardSlot) payload.getObject();
        if (target != null) {
            final CardSlot targetSlot = (CardSlot) target.getActor();

            if (targetSlot.isDisabled()) {
                //do not allow dropping on disabled slots (both drawn and hand)
                source.setCard(payloadSlot.getCard());
                return;
            }

            if (targetSlot.getCard() == null) {
                //move the payload to the target slot
                targetSlot.setCard(payloadSlot.getCard());

            } else {
                //swap the two items
                final Card payloadCard = payloadSlot.getCard();

                source.setCard(targetSlot.getCard());
                targetSlot.setCard(payloadCard);
            }

            //fire an enter event to display tooltip when dropping the card
            Vector2 tempCoords = new Vector2();
            GameScreen.getUiHandler().getStage().screenToStageCoordinates(tempCoords.set(Gdx.input.getX(), Gdx.input.getY()));
            InputEvent inputEvent = new InputEvent();
            inputEvent.setType(InputEvent.Type.enter);
            inputEvent.setPointer(-1);
            inputEvent.setStageX(tempCoords.x);
            inputEvent.setStageY(tempCoords.y);
            targetSlot.fire(inputEvent);
        } else {
            // If card is dropped outside a card slot we put it back to its original slot
            source.setCard(payloadSlot.getCard());
        }
    }
}
