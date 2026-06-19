package com.example.btl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HauiExam.db";

    private static final int DATABASE_VERSION = 25;
    private final Context context;

    // --- BẢNG USERS ---
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NAME = "username";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_FULLNAME = "fullname";
    public static final String COL_USER_PROGRESS = "progress";

    // --- BẢNG QUESTIONS ---
    public static final String TABLE_QUESTIONS = "questions";
    public static final String COL_Q_ID = "id";
    public static final String COL_Q_CONTENT = "content";
    public static final String COL_Q_OPTION_A = "option_a";
    public static final String COL_Q_OPTION_B = "option_b";
    public static final String COL_Q_OPTION_C = "option_c";
    public static final String COL_Q_OPTION_D = "option_d";
    public static final String COL_Q_ANSWER = "answer";
    public static final String COL_Q_CHAPTER = "chapter";

    // --- BẢNG DOCUMENTS ---
    public static final String TABLE_DOCUMENTS = "documents";
    public static final String COL_DOC_ID = "id";
    public static final String COL_DOC_TITLE = "title";
    public static final String COL_DOC_CONTENT = "content";

    // --- BẢNG EXAMS VÀ EXAM_QUESTIONS ---
    public static final String TABLE_EXAMS = "exams";
    public static final String TABLE_EXAM_QUESTIONS = "exam_questions";

    public static final String TABLE_USER_RESULTS = "user_results";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_FULLNAME + " TEXT, " +
                COL_USER_PROGRESS + " INTEGER DEFAULT 0)");

        db.execSQL("CREATE TABLE " + TABLE_QUESTIONS + " (" +
                COL_Q_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_Q_CONTENT + " TEXT, " +
                COL_Q_OPTION_A + " TEXT, " +
                COL_Q_OPTION_B + " TEXT, " +
                COL_Q_OPTION_C + " TEXT, " +
                COL_Q_OPTION_D + " TEXT, " +
                COL_Q_ANSWER + " TEXT, " +
                COL_Q_CHAPTER + " INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_DOCUMENTS + " (" +
                COL_DOC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DOC_TITLE + " TEXT, " +
                COL_DOC_CONTENT + " TEXT, " +
                "user_id INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_EXAMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "user_id INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_EXAM_QUESTIONS + " (" +
                "exam_id INTEGER, " +
                "question_id INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_USER_RESULTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "exam_id INTEGER, " +
                "score REAL, " +
                "completed_at DATETIME DEFAULT CURRENT_TIMESTAMP)");

        insertInitialData(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        ContentValues admin = new ContentValues();
        admin.put(COL_USER_NAME, "user");
        admin.put(COL_USER_PASSWORD, hashPassword("123"));
        admin.put(COL_USER_FULLNAME, "Người dùng HaUI");
        db.insert(TABLE_USERS, null, admin);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAM_QUESTIONS);
        // --- THÊM Ở ĐÂY: Xóa bảng user_results khi upgrade DB ---
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_RESULTS);
        onCreate(db);
    }

    public void importDocumentsFromCSV() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_DOCUMENTS, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            if (count > 0) return;
        }

        try {
            InputStream is = context.getAssets().open("Documents.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            reader.readLine();

            db.beginTransaction();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] d = line.split(";", 2);
                if (d.length >= 2) {
                    ContentValues v = new ContentValues();
                    v.put(COL_DOC_TITLE, d[0].trim());
                    v.put(COL_DOC_CONTENT, d[1].trim().replace("\\n", "\n"));
                    db.insert(TABLE_DOCUMENTS, null, v);
                }
            }
            db.setTransactionSuccessful();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void addDocument(String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_DOC_TITLE, title);
        v.put(COL_DOC_CONTENT, content);
        db.insert(TABLE_DOCUMENTS, null, v);
    }

    public List<String[]> searchDocuments(String keyword) {
        List<String[]> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_DOC_ID + ", " + COL_DOC_TITLE + ", " + COL_DOC_CONTENT +
                " FROM " + TABLE_DOCUMENTS +
                " WHERE " + COL_DOC_TITLE + " LIKE ? OR " + COL_DOC_CONTENT + " LIKE ?";
        Cursor c = db.rawQuery(query, new String[]{"%" + keyword + "%", "%" + keyword + "%"});

        if (c.moveToFirst()) {
            do {
                list.add(new String[]{String.valueOf(c.getInt(0)), c.getString(1), c.getString(2)});
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public void importQuestionsFromCSV() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_QUESTIONS, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            if (count > 0) return;
        }

        try {
            InputStream is = context.getAssets().open("Quesstion_categorized.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            reader.readLine();

            db.beginTransaction();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] q = line.split(";");
                if (q.length >= 7) {
                    ContentValues v = new ContentValues();
                    v.put(COL_Q_CONTENT, q[0].trim());
                    v.put(COL_Q_OPTION_A, q[1].trim());
                    v.put(COL_Q_OPTION_B, q[2].trim());
                    v.put(COL_Q_OPTION_C, q[3].trim());
                    v.put(COL_Q_OPTION_D, q[4].trim());
                    v.put(COL_Q_ANSWER, q[5].trim());
                    v.put(COL_Q_CHAPTER, Integer.parseInt(q[6].trim()));
                    db.insert(TABLE_QUESTIONS, null, v);
                }
            }
            db.setTransactionSuccessful();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    public List<Question> getRandomQuestionsByChapter(int totalQuestionsRequested) {
        List<Question> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        int totalChapters = 6;
        int baseQuestionsPerChapter = totalQuestionsRequested / totalChapters;
        int remainder = totalQuestionsRequested % totalChapters;

        for (int i = 1; i <= totalChapters; i++) {
            int limitForThisChapter = baseQuestionsPerChapter + (i <= remainder ? 1 : 0);

            if (limitForThisChapter > 0) {
                Cursor c = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONS + " WHERE " + COL_Q_CHAPTER + " = ? ORDER BY RANDOM() LIMIT ?",
                        new String[]{String.valueOf(i), String.valueOf(limitForThisChapter)});
                if (c.moveToFirst()) {
                    do {
                        list.add(new Question(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getInt(7)));
                    } while (c.moveToNext());
                }
                c.close();
            }
        }
        Collections.shuffle(list);
        return list;
    }

    public List<Question> getRandomQuestions(int limit) {
        List<Question> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONS + " ORDER BY RANDOM() LIMIT " + limit, null);
        if (c.moveToFirst()) {
            do {
                list.add(new Question(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getInt(7)));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<Question> getAllQuestions() {
        List<Question> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_QUESTIONS, null);
        if (c.moveToFirst()) {
            do {
                list.add(new Question(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getInt(7)));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public void addQuestion(Question q) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_Q_CONTENT, q.getContent());
        v.put(COL_Q_OPTION_A, q.getOptionA());
        v.put(COL_Q_OPTION_B, q.getOptionB());
        v.put(COL_Q_OPTION_C, q.getOptionC());
        v.put(COL_Q_OPTION_D, q.getOptionD());
        v.put(COL_Q_ANSWER, q.getAnswer());
        v.put(COL_Q_CHAPTER, q.getChapter());
        db.insert(TABLE_QUESTIONS, null, v);
    }

    public void updateQuestion(Question q) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_Q_CONTENT, q.getContent());
        v.put(COL_Q_OPTION_A, q.getOptionA());
        v.put(COL_Q_OPTION_B, q.getOptionB());
        v.put(COL_Q_OPTION_C, q.getOptionC());
        v.put(COL_Q_OPTION_D, q.getOptionD());
        v.put(COL_Q_ANSWER, q.getAnswer());
        v.put(COL_Q_CHAPTER, q.getChapter());
        db.update(TABLE_QUESTIONS, v, COL_Q_ID + "=?", new String[]{String.valueOf(q.getId())});
    }

    public void deleteQuestion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_QUESTIONS, COL_Q_ID + "=?", new String[]{String.valueOf(id)});
    }


    public long createSavedExam(String title, int totalQuestionsRequested) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);

        long examId = -1;
        db.beginTransaction();
        try {
            examId = db.insert(TABLE_EXAMS, null, values);
            if (examId != -1) {
                List<Question> questions = getRandomQuestionsByChapter(totalQuestionsRequested);
                for (Question q : questions) {
                    ContentValues eqValues = new ContentValues();
                    eqValues.put("exam_id", examId);
                    eqValues.put("question_id", q.getId());
                    db.insert(TABLE_EXAM_QUESTIONS, null, eqValues);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return examId;
    }

    public List<Exam> getAllExams() {
        List<Exam> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT id, title, created_at FROM " + TABLE_EXAMS + " ORDER BY id DESC", null);
        if (c.moveToFirst()) {
            do {
                list.add(new Exam(c.getInt(0), c.getString(1), c.getString(2)));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public List<Question> getQuestionsForExam(int examId) {
        List<Question> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT q.* FROM " + TABLE_QUESTIONS + " q " +
                "INNER JOIN " + TABLE_EXAM_QUESTIONS + " eq ON q.id = eq.question_id " +
                "WHERE eq.exam_id = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(examId)});
        if (c.moveToFirst()) {
            do {
                list.add(new Question(c.getInt(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getInt(7)));
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public int getUserProgress(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_USER_PROGRESS + " FROM " + TABLE_USERS + " WHERE " + COL_USER_NAME + "=?", new String[]{username});
        int p = 0;
        if (c.moveToFirst()) p = c.getInt(0);
        c.close();
        return p;
    }

    public void updateUserProgress(String username, int progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_USER_PROGRESS, progress);
        db.update(TABLE_USERS, v, COL_USER_NAME + "=?", new String[]{username});
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPass = hashPassword(password);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_NAME + "=? AND " + COL_USER_PASSWORD + "=?", new String[]{username, hashedPass});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean registerUser(String username, String password, String fullname) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, username);
        values.put(COL_USER_PASSWORD, hashPassword(password));
        values.put(COL_USER_FULLNAME, fullname);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public static String hashPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return password;
        }
    }

    public List<String[]> getAllDocuments() {
        List<String[]> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_DOC_ID + ", " + COL_DOC_TITLE + ", " + COL_DOC_CONTENT + " FROM " + TABLE_DOCUMENTS + " ORDER BY " + COL_DOC_ID + " DESC", null);
        if (c.moveToFirst()) {
            do {
                list.add(new String[]{String.valueOf(c.getInt(0)), c.getString(1), c.getString(2)});
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public void updateDocument(int id, String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_DOC_TITLE, title);
        v.put(COL_DOC_CONTENT, content);
        db.update(TABLE_DOCUMENTS, v, COL_DOC_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void deleteDocument(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOCUMENTS, COL_DOC_ID + "=?", new String[]{String.valueOf(id)});
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_NAME + "=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }


}