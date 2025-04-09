-- Table of moods
CREATE TABLE moods (

    mood_id INTEGER PRIMARY KEY AUTOINCREMENT,
    mood TEXT NOT NULL
);

-- Inserting the predefined moods we chose
INSERT INTO moods (mood) VALUES 
('Relaxed'), 
('Content'), 
('Optimistic'), 
('Excited'), 
('Lonely'), 
('Depressed'), 
('Distant'), 
('Disappointed'), 
('Frustrated'), 
('Anxious'), 
('Scared'), 
('Insecure'), 
('Tired'), 
('Stressed'), 
('Bored');

-- Table for user mood entries
CREATE TABLE mood_entries (

    entry_id INTEGER PRIMARY KEY AUTOINCREMENT,
    mood_id INTEGER NOT NULL,
    tag TEXT,
    entry_date_and_time TEXT NOT NULL,
    FOREIGN KEY (mood_id) REFERENCES moods(mood_id)
);

-- Table for user journal entries
CREATE TABLE journal (

    journal_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    text_entry TEXT NOT NULL,
    entry_data_and_time TEXT NOT NULL

);

-- Table of the guided meditation/breathing exercises
CREATE TABLE exercises (

    exercise_id INTEGER PRIMARY KEY AUTOINCREMENT,
    exercise_name TEXT NOT NULL,
    exercise_description TEXT

);

-- Inserting the predefined exercises we chose
INSERT INTO exercises (exercise_name, exercise_description) VALUES
('Deep Breathing', 'Traditional 4-7-8 deep breathing with visual elements, optional timer, and optional audio.'),
('Progressive Muscle Relaxation', 'Guided meditation designed to help relax your body from head to toe. Includes audio and visual elements.'),
('Box Breathing', '4-4-4 breathing technique used by military and law enforcement often to manage stress and improve focus in intense situations. Includes visual elements, optional timer, and optional audio.');


-- Table for user guided meditation/breathing exercise pre and post logs
CREATE TABLE exercise_entries (

    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
    exercise_id INTEGER NOT NULL,
    mood_before_id INTEGER,
    mood_after_id INTEGER,
    start_time TEXT NOT NULL,
    end_time TEXT NOT NULL,

    FOREIGN KEY (exercise_id) REFERENCES exercises(exercise_id),
    FOREIGN KEY (mood_before_id) REFERENCES moods(mood_id),
    FOREIGN KEY (mood_after_id) REFERENCES moods(mood_id)

);

-- Table of user favorited resources
CREATE TABLE favorite_resources (

    resource_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    phone_number TEXT,
    website TEXT

);