package cat.xlagunas.andrtc.view.model;

public abstract class BaseEntity {

    public final static int TEAM_ENTITY = 0;
    public final static int GAME_DAY_ENTITY_GENERAL = 1;
    public final static int GAME_DAY_ENTITY_DETAILED = 2;
    public final static int TEAM_DETAILS_ENTITY = 3;

    public abstract int getEntityType();

}