-- Create Database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'campusFlexDb')
BEGIN
    CREATE DATABASE campusFlexDb;
END
GO

USE campusFlexDb;
GO

-- Create playerStats table
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[playerStats]') AND type in (N'U'))
BEGIN
    CREATE TABLE playerStats (
        id INT PRIMARY KEY,
        gpa FLOAT,
        energy INT,
        stress INT,
        karma INT,
        xLocation INT,
        yLocation INT
    );
END
GO

-- Seed dummy data
IF NOT EXISTS (SELECT * FROM playerStats WHERE id = 1)
BEGIN
    INSERT INTO playerStats (id, gpa, energy, stress, karma, xLocation, yLocation)
    VALUES (1, 3.5, 100, 0, 10, 0, 0);
END
GO
