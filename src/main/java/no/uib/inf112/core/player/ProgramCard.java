package no.uib.inf112.core.player;

public class ProgramCard implements Card {

    private final Movement ACTION;
    private final int PRIORITY;

    /**
     * @param action The movement the card will impose
     * @param priority The unique priority of the card
     */
    public ProgramCard(Movement action, int priority) {
        this.ACTION = action;
        this.PRIORITY = priority;
    }

    /**
     * @return The action (movement) imposed by this card
     */
    public Movement getAction() {
        return this.ACTION;
    }

    /**
     * @return The priority of this card
     */
    public int getPriority() {
        return this.PRIORITY;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
