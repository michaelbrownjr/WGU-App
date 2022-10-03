package database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import activities.CourseActivity;
import dao.AssessmentDao;
import dao.CourseDao;
import dao.NoteDao;
import dao.TermDao;
import entities.AssessmentEntity;
import entities.CourseEntity;
import entities.NoteEntity;
import entities.TermEntity;

@Database(version = 3,
        entities = {
                TermEntity.class,
                CourseEntity.class,
                NoteEntity.class,
                AssessmentEntity.class
        })
public abstract class C196Database extends RoomDatabase {
    private static C196Database instance;

    public abstract TermDao termDao();

    public abstract CourseDao courseDao();

    public abstract NoteDao noteDao();

    public abstract AssessmentDao assessmentDao();

    public static synchronized C196Database getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    C196Database.class, "C196_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private TermDao termDao;
        private CourseDao courseDao;
        private AssessmentDao assessmentDao;
        private NoteDao noteDao;

        public PopulateDbAsyncTask(C196Database db) {
            termDao = db.termDao();
            courseDao = db.courseDao();
            assessmentDao = db.assessmentDao();
            noteDao = db.noteDao();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            termDao.insert(new TermEntity("Term 1", "01/01/2020", "06/06/2020"));
            termDao.insert(new TermEntity("Term 2", "01/01/2021", "06/06/2022"));
            termDao.insert(new TermEntity("Term 3", "01/01/2022", "06/06/2023"));

            courseDao.insert(new CourseEntity(1, "Course 1", "01/01/2020", "06/05/2020",
                    true, false, CourseActivity.STATUS_IN_PROGRESS, "Bob", "5555555555", "bob@null.com"));
            courseDao.insert(new CourseEntity(1, "Course 2", "01/01/2020", "06/05/2020",
                    false, false, CourseActivity.STATUS_COMPLETED, "Bob", "5555555555", "bob@null.com"));
            courseDao.insert(new CourseEntity(2, "Course 3", "01/01/2020", "06/05/2020",
                    false, false,CourseActivity.STATUS_PLANNED, "Bob", "5555555555", "bob@null.com"));

            assessmentDao.insert(new AssessmentEntity(1, "test1", 0, "03/03/2020", "01/02/2020", true, false));
            assessmentDao.insert(new AssessmentEntity(2, "test1", 0, "03/03/2020", "01/02/2020", false, true));
            assessmentDao.insert(new AssessmentEntity(3, "test1", 1, "03/03/2020", "01/02/2020", true, true));

            noteDao.insert(new NoteEntity(1, "Note for course 1", "Content of my note"));
            noteDao.insert(new NoteEntity(2, "Note for course 2", "Content of my note"));
            noteDao.insert(new NoteEntity(3, "Note for course 3", "Content of my note"));
            noteDao.insert(new NoteEntity(3, "Notes for course 3", "Content of my note"));
            return null;
        }
    }
}
