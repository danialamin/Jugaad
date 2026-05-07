package database;

/**
 * HibernateUtil - Utility class for Hibernate ORM SessionFactory management.
 * 
 * NOTE: This class is a placeholder for future Hibernate ORM integration.
 * Currently, SQLServerHandler uses raw JDBC for database operations.
 * When Hibernate dependencies (jakarta.persistence, hibernate-core) are added
 * to the classpath via Maven/Gradle, this class can be activated to provide
 * ORM-based persistence using annotated entity classes.
 */
public class HibernateUtil {
    // Hibernate SessionFactory would be initialized here when dependencies are available.
    // For now, SQLServerHandler uses direct JDBC with PreparedStatements.
}
