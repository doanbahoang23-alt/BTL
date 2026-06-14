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
    // Tăng version lên 23
    private static final int DATABASE_VERSION = 23;
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
                COL_DOC_CONTENT + " TEXT)");

        // Bảng lưu thông tin đề thi
        db.execSQL("CREATE TABLE " + TABLE_EXAMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)");

        // Bảng nối đề thi và câu hỏi
        db.execSQL("CREATE TABLE " + TABLE_EXAM_QUESTIONS + " (" +
                "exam_id INTEGER, " +
                "question_id INTEGER)");

        insertInitialData(db);
    }

    private void insertInitialData(SQLiteDatabase db) {
        ContentValues admin = new ContentValues();
        admin.put(COL_USER_NAME, "user");
        admin.put(COL_USER_PASSWORD, hashPassword("123"));
        admin.put(COL_USER_FULLNAME, "Người dùng HaUI");
        db.insert(TABLE_USERS, null, admin);

        String[][] docs = {

                {"Chương 1: Tổng quan về Kỹ nghệ Phần mềm (A-Z)",

                        "1. KHÁI NIỆM VỀ PHẦN MỀM:\n" +

                                "- Phần mềm máy tính không chỉ là code. Nó bao gồm: các chương trình máy tính, dữ liệu cấu hình để thực hiện đúng chức năng, và các tài liệu mô tả (System documentation, User documentation).\n" +

                                "- Phần mềm có đặc tính: Được xây dựng (engineered), không phải sản xuất vật lý (manufactured). Nó không bị mòn đi nhưng sẽ thoái hóa theo thời gian do các thay đổi và cập nhật không đồng bộ.\n\n" +

                                "2. ĐỊNH NGHĨA KỸ NGHỆ PHẦN MỀM (SE):\n" +

                                "- SE là một kỷ luật kỹ thuật liên quan đến mọi khía cạnh của sản xuất phần mềm. Nó áp dụng các lý thuyết, phương pháp và công cụ để tạo ra phần mềm chất lượng, đáng tin cậy và kinh tế.\n\n" +

                                "3. CÁC ĐẶC TRƯNG CỦA PHẦN MỀM CHẤT LƯỢNG CAO:\n" +

                                "- Khả năng bảo trì: Có thể tiến hóa để đáp ứng nhu cầu thay đổi của khách hàng.\n" +

                                "- Độ tin cậy và An toàn: Không gây ra thiệt hại về người và của khi có sự cố.\n" +

                                "- Hiệu năng: Sử dụng tối ưu CPU, RAM, băng thông.\n" +

                                "- Khả năng sử dụng: Giao diện phù hợp, dễ làm quen.\n\n" +

                                "4. CÁC HUYỀN THOẠI VỀ PHẦN MỀM (Software Myths):\n" +

                                "- Huyền thoại quản lý: 'Chúng ta có sách hướng dẫn rồi, thế là đủ'. Thực tế: Sách hướng dẫn thường lạc hậu và không áp dụng được ngay.\n" +

                                "- Huyền thoại khách hàng: 'Yêu cầu thay đổi liên tục cũng không sao vì phần mềm rất linh hoạt'. Thực tế: Thay đổi muộn gây chi phí cực lớn.\n" +

                                "- Huyền thoại lập trình viên: 'Xong code là xong việc'. Thực tế: 60-80% nỗ lực nằm ở giai đoạn sau khi bàn giao." +
                                "- Link tham khảo: https://www.google.com/search?q=https://www.geeksforgeeks.org/software-engineering-software-development-life-cycle-sdlc/"},



                {"Chương 2: Quy trình phát triển phần mềm - Tổng hợp mô hình",

                        "1. CÁC HOẠT ĐỘNG KHUNG (Framework Activities):\n" +

                                "- Giao tiếp (Communication): Phân tích khách hàng.\n" +

                                "- Lập kế hoạch (Planning): Dự báo rủi ro, thời gian, kinh phí.\n" +

                                "- Mô hình hóa (Modeling): Phân tích và thiết kế.\n" +

                                "- Xây dựng (Construction): Viết mã và Unit Test.\n" +

                                "- Triển khai (Deployment): Bàn giao và bảo trì.\n\n" +

                                "2. CÁC MÔ HÌNH TIẾN TRÌNH:\n" +

                                "- Mô hình Thác nước (Waterfall): Tuần tự, rõ ràng. Phù hợp dự án có yêu cầu không đổi.\n" +

                                "- Mô hình chữ V: Nhấn mạnh việc kiểm thử song song với từng giai đoạn thiết kế.\n" +

                                "- Mô hình Prototyping: Xây dựng bản mẫu nhanh để lấy phản hồi từ khách hàng.\n" +

                                "- Mô hình Xoắn ốc (Spiral Model): Chia thành nhiều vòng lặp, mỗi vòng lặp chú trọng đánh giá rủi ro.\n\n" +

                                "3. PHÁT TRIỂN LINH HOẠT (AGILE):\n" +

                                "- Scrum: Sử dụng các Sprint (vòng lặp ngắn). Vai trò chính: Product Owner (người nắm giữ yêu cầu), Scrum Master (người hỗ trợ đội ngũ), Team (đội ngũ phát triển).\n" +

                                "- XP (Extreme Programming): Lập trình cặp (Pair programming), Kiểm thử trước khi code (TDD)." +
                                "" +
                                "- Link tham khảo: https://www.google.com/search?q=https://www.geeksforgeeks.org/software-engineering-software-development-life-cycle-sdlc/"},



                {"Chương 3: Phân tích yêu cầu - Kỹ thuật lấy yêu cầu",

                        "1. PHÂN LOẠI YÊU CẦU:\n" +

                                "- Yêu cầu chức năng: Hệ thống phải làm gì (Đăng nhập, tìm kiếm, xuất báo cáo).\n" +

                                "- Yêu cầu phi chức năng: Hệ thống phải đạt tiêu chuẩn gì (Tải trang < 2 giây, hỗ trợ 5000 người dùng đồng thời, bảo mật SSL).\n\n" +

                                "2. QUY TRÌNH KỸ NGHỆ YÊU CẦU:\n" +

                                "- Khảo sát (Elicitation): Phỏng vấn, điều tra bằng bảng hỏi, quan sát hiện trường.\n" +

                                "- Triển khai (Elaboration): Tạo ra các sơ đồ phân tích ban đầu.\n" +

                                "- Thương thảo (Negotiation): Thống nhất ưu tiên các yêu cầu quan trọng trước.\n" +

                                "- Đặc tả (Specification): Viết tài liệu SRS (Software Requirements Specification).\n" +

                                "- Kiểm chứng (Validation): Đảm bảo các yêu cầu không mâu thuẫn lẫn nhau.\n\n" +

                                "3. CÁC SAI LẦM THƯỜNG GẶP:\n" +

                                "- Không hiểu rõ quy trình nghiệp vụ của khách hàng.\n" +

                                "- Yêu cầu quá mơ hồ dẫn đến hiểu lầm giữa DEV và khách hàng." +
                                "- Link tham khảo: https://www.tutorialspoint.com/software_engineering/software_requirements.htm"},



                {"Chương 4: Thiết kế hệ thống & UML chuyên sâu",

                        "1. THIẾT KẾ KIẾN TRÚC:\n" +

                                "- Kiến trúc phân tầng (Layered): Presentation -> Business Logic -> Data Access.\n" +

                                "- Kiến trúc Client-Server: Tận dụng sức mạnh xử lý của cả máy khách và máy chủ.\n" +

                                "- Microservices: Chia nhỏ hệ thống thành các dịch vụ độc lập, dễ mở rộng.\n\n" +

                                "2. MÔ HÌNH HÓA VỚI UML 2.0:\n" +

                                "- Sơ đồ Use Case: Mô tả chức năng và các tác nhân (User, Admin, System).\n" +

                                "- Sơ đồ Lớp (Class Diagram): Thể hiện cấu trúc các lớp và mối quan hệ (Inheritance, Association, Composition).\n" +

                                "- Sơ đồ Trình tự (Sequence Diagram): Thể hiện sự tương tác giữa các đối tượng theo thời gian thực.\n" +

                                "- Sơ đồ Hoạt động (Activity Diagram): Thể hiện luồng nghiệp vụ như một lưu đồ.\n\n" +

                                "3. NGUYÊN TẮC THIẾT KẾ TỐT:\n" +

                                "- Trừu tượng hóa, Tính module, Che giấu thông tin, Tinh lọc."},



                {"Chương 5: Kiểm thử và Quản lý chất lượng",

                        "1. CHIẾN LƯỢC KIỂM THỬ:\n" +

                                "- Unit Test: Kiểm thử ở mức thấp nhất, do lập trình viên thực hiện.\n" +

                                "- Integration Test: Kiểm tra sự kết nối giữa các module khác nhau.\n" +

                                "- System Test: Kiểm tra toàn bộ phần mềm trên môi trường giống thật.\n" +

                                "- Acceptance Test: Khách hàng kiểm tra để xác nhận thanh toán hợp đồng.\n\n" +

                                "2. KỸ THUẬT KIỂM THỬ:\n" +

                                "- Kiểm thử hộp đen (Black-box): Dựa trên đầu vào và đầu ra, không quan tâm code.\n" +

                                "- Kiểm thử hộp trắng (White-box): Dựa trên luồng logic bên trong mã nguồn.\n\n" +

                                "3. KIỂM THỬ HỒI QUY (Regression Testing):\n" +

                                "- Thực hiện lại các bài test cũ sau khi cập nhật phần mềm để đảm bảo không có lỗi mới phát sinh ở những tính năng cũ." +
                                "- Link tham khảo: https://www.guru99.com/software-testing.html"},



                {"Chương 6: Quản trị dự án, Chi phí và Bảo trì",

                        "1. QUẢN LÝ DỰ ÁN:\n" +

                                "- Lập kế hoạch: Phân chia công việc (WBS), sơ đồ Gantt để theo dõi tiến độ.\n" +

                                "- Quản lý rủi ro: Nhận diện rủi ro (nhân viên nghỉ việc, công nghệ thay đổi) và lập phương án dự phòng.\n\n" +

                                "2. ƯỚC LƯỢNG CHI PHÍ:\n" +

                                "- Mô hình COCOMO II: Ước lượng dựa trên kích thước phần mềm và các nhân số nỗ lực.\n" +

                                "- Điểm chức năng (Function Point): Ước lượng dựa trên các thành phần chức năng (Inputs, Outputs, Inquiries, Files, Interfaces).\n\n" +

                                "3. BẢO TRÌ PHẦN MỀM:\n" +

                                "- Bảo trì sửa lỗi (Corrective).\n" +

                                "- Bảo trì thích nghi (Adaptive): Thay đổi để chạy trên OS mới.\n" +

                                "- Bảo trì hoàn thiện (Perfective): Thêm tính năng mới.\n" +

                                "- Bảo trì phòng ngừa (Preventive): Viết lại code cho sạch sẽ, dễ hiểu hơn." +
                                "- Link tham khảo: https://www.geeksforgeeks.org/software-engineering/software-engineering-software-project-management-spm/"}

        };
        for (String[] doc : docs) {
            ContentValues v = new ContentValues();
            v.put(COL_DOC_TITLE, doc[0]);
            v.put(COL_DOC_CONTENT, doc[1]);
            db.insert(TABLE_DOCUMENTS, null, v);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAM_QUESTIONS);
        onCreate(db);
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
        Cursor c = db.rawQuery("SELECT " + COL_DOC_TITLE + ", " + COL_DOC_CONTENT + " FROM " + TABLE_DOCUMENTS, null);
        if (c.moveToFirst()) {
            do {
                list.add(new String[]{c.getString(0), c.getString(1)});
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COL_USER_NAME + "=?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}