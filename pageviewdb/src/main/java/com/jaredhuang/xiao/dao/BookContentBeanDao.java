package com.jaredhuang.xiao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jaredhuang.xiao.bean.BookContentBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BOOK_CONTENT_BEAN".
*/
public class BookContentBeanDao extends AbstractDao<BookContentBean, String> {

    public static final String TABLENAME = "BOOK_CONTENT_BEAN";

    /**
     * Properties of entity BookContentBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property NoteUrl = new Property(0, String.class, "noteUrl", false, "NOTE_URL");
        public final static Property DurChapterUrl = new Property(1, String.class, "durChapterUrl", true, "DUR_CHAPTER_URL");
        public final static Property DurChapterIndex = new Property(2, Integer.class, "durChapterIndex", false, "DUR_CHAPTER_INDEX");
        public final static Property DurChapterContent = new Property(3, String.class, "durChapterContent", false, "DUR_CHAPTER_CONTENT");
        public final static Property Domain = new Property(4, String.class, "domain", false, "DOMAIN");
        public final static Property TimeMillis = new Property(5, Long.class, "timeMillis", false, "TIME_MILLIS");
    }


    public BookContentBeanDao(DaoConfig config) {
        super(config);
    }
    
    public BookContentBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BOOK_CONTENT_BEAN\" (" + //
                "\"NOTE_URL\" TEXT," + // 0: noteUrl
                "\"DUR_CHAPTER_URL\" TEXT PRIMARY KEY NOT NULL ," + // 1: durChapterUrl
                "\"DUR_CHAPTER_INDEX\" INTEGER," + // 2: durChapterIndex
                "\"DUR_CHAPTER_CONTENT\" TEXT," + // 3: durChapterContent
                "\"DOMAIN\" TEXT," + // 4: domain
                "\"TIME_MILLIS\" INTEGER);"); // 5: timeMillis
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BOOK_CONTENT_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, BookContentBean entity) {
        stmt.clearBindings();
 
        String noteUrl = entity.getNoteUrl();
        if (noteUrl != null) {
            stmt.bindString(1, noteUrl);
        }
 
        String durChapterUrl = entity.getDurChapterUrl();
        if (durChapterUrl != null) {
            stmt.bindString(2, durChapterUrl);
        }
 
        Integer durChapterIndex = entity.getDurChapterIndex();
        if (durChapterIndex != null) {
            stmt.bindLong(3, durChapterIndex);
        }
 
        String durChapterContent = entity.getDurChapterContent();
        if (durChapterContent != null) {
            stmt.bindString(4, durChapterContent);
        }
 
        String domain = entity.getDomain();
        if (domain != null) {
            stmt.bindString(5, domain);
        }
 
        Long timeMillis = entity.getTimeMillis();
        if (timeMillis != null) {
            stmt.bindLong(6, timeMillis);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, BookContentBean entity) {
        stmt.clearBindings();
 
        String noteUrl = entity.getNoteUrl();
        if (noteUrl != null) {
            stmt.bindString(1, noteUrl);
        }
 
        String durChapterUrl = entity.getDurChapterUrl();
        if (durChapterUrl != null) {
            stmt.bindString(2, durChapterUrl);
        }
 
        Integer durChapterIndex = entity.getDurChapterIndex();
        if (durChapterIndex != null) {
            stmt.bindLong(3, durChapterIndex);
        }
 
        String durChapterContent = entity.getDurChapterContent();
        if (durChapterContent != null) {
            stmt.bindString(4, durChapterContent);
        }
 
        String domain = entity.getDomain();
        if (domain != null) {
            stmt.bindString(5, domain);
        }
 
        Long timeMillis = entity.getTimeMillis();
        if (timeMillis != null) {
            stmt.bindLong(6, timeMillis);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1);
    }    

    @Override
    public BookContentBean readEntity(Cursor cursor, int offset) {
        BookContentBean entity = new BookContentBean( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // noteUrl
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // durChapterUrl
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // durChapterIndex
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // durChapterContent
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // domain
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5) // timeMillis
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, BookContentBean entity, int offset) {
        entity.setNoteUrl(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setDurChapterUrl(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDurChapterIndex(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setDurChapterContent(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDomain(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTimeMillis(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
     }
    
    @Override
    protected final String updateKeyAfterInsert(BookContentBean entity, long rowId) {
        return entity.getDurChapterUrl();
    }
    
    @Override
    public String getKey(BookContentBean entity) {
        if(entity != null) {
            return entity.getDurChapterUrl();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(BookContentBean entity) {
        return entity.getDurChapterUrl() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
