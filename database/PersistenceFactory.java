package database;

public class PersistenceFactory {
    
    public enum PersistenceType {
        SQL_SERVER, XML
    }

    public static PersistenceHandler getPersistenceHandler(PersistenceType type) {
        switch (type) {
            case SQL_SERVER:
                return new SQLServerHandler();
            case XML:
                return new XMLPersistenceHandler();
            default:
                throw new IllegalArgumentException("Unknown persistence type: " + type);
        }
    }
}
