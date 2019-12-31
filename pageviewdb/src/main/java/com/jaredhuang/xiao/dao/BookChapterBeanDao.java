package com.jaredhuang.xiao.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jaredhuang.xiao.bean.BookChapterBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "BOOK_CHAPTER_BEAN".
*/
public class BookChapterBeanDao extends AbstractDao<BookChapterBean, String> {

    public static final String TABLENAME = "BOOK_CHAPTER_BEAN";

    /**
     * Properties of entity BookChapterBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Domain = new Property(0, String.class, "domain", false, "DOMAIN");
        public final static Property NoteUrl = new Property(1, String.class, "noteUrl", false, "NOTE_URL");
        public final static Property DurChapterIndex = new Property(2, int.class, "durChapterIndex", false, "DUR_CHAPTER_INDEX");
        public final static Property DurChapterUrl = new Property(3, String.class, "durChapterUrl", true, "DUR_CHAPTER_URL");
        public final static Property DurChapterName = new Property(4, String.class, "durChapterName", false, "DUR_CHAPTER_NAME");
        public final static Property Start = new Property(5, Long.class, "start", false, "START");
        public final static Property End = new Property(6, Long.class, "end", false, "END");
    }


    public BookChapterBeanDao(DaoConfig config) {
        super(config);
    }
    
    public BookChapterBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"BOOK_CHAPTER_BEAN\" (" + //
                "\"DOMAIN\" TEXT," + // 0: domain
                "\"NOTE_URL\" TEXT," + // 1: noteUrl
                "\"DUR_CHAPTER_INDEX\" INTEGER NOT NULL ," + // 2: durChapterIndex
                "\"DUR_CHAPTER_URL\" TEXT PRIMARY KEY NOT NULL ," + // 3: durChapterUrl
                "\"DUR_CHAPTER_NAME\" TEXT," + // 4: durChapterName
                "\"START\" INTEGER," + // 5: start
                "\"END\" INTEGER);"); // 6: end
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"BOOK_CHAPTER_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, BookChapterBean entity) {
        stmt.clearBindings();
 
        String domain = entity.getDomain();
        if (domain != null) {
            stmt.bindString(1, domain);
        }
 
        String noteUrl = entity.getNoteUrl();
        if (noteUrl != null) {
            stmt.bindString(2, noteUrl);
        }
        stmt.bindLong(3, entity.getDurChapterIndex());
 
        String durChapterUrl = entity.getDurChapterUrl();
        if (durChapterUrl != null) {
            stmt.bindString(4, durChapterUrl);
        }
 
        String durChapterName = entity.getDurChapterName();
        if (durChapterName != null) {
            stmt.bindString(5, durChapterName);
        }
 
        Long start = entity.getStart();
        if (start != null) {
            stmt.bindLong(6, start);
        }
 
        Long end = entity.getEnd();
        if (end != null) {
            stmt.bindLong(7, end);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, BookChapterBean entity) {
        stmt.clearBindings();
 
        String domain = entity.getDomain();
        if (domain != null) {
            stmt.bindString(1, domain);
        }
 
        String noteUrl = entity.getNoteUrl();
        if (noteUrl != null) {
            stmt.bindString(2, noteUrl);
        }
        stmt.bindLong(3, entity.getDurChapterIndex());
 
        String durChapterUrl = entity.getDurChapterUrl();
        if (durChapterUrl != null) {
            stmt.bindString(4, durChapterUrl);
        }
 
        String durChapterName = entity.getDurChapterName();
        if (durChapterName != null) {
            stmt.bindString(5, durChapterName);
        }
 
        Long start = entity.getStart();
        if (start != null) {
            stmt.bindLong(6, start);
        }
 
        Long end = entity.getEnd();
        if (end != null) {
            stmt.bindLong(7, end);
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3);
    }    

    @Override
    public BookChapterBean readEntity(Cursor cursor, int offset) {
        BookChapterBean entity = new BookChapterBean( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // domain
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // noteUrl
            cursor.getInt(offset + 2), // durChapterIndex
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // durChapterUrl
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // durChapterName
            cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5), // start
            cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6) // end
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, BookChapterBean entity, int offset) {
        entity.setDomain(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setNoteUrl(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDurChapterIndex(cursor.getInt(offset + 2));
        entity.setDurChapterUrl(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDurChapterName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStart(cursor.isNull(offset + 5) ? null : cursor.getLong(offset + 5));
        entity.setEnd(cursor.isNull(offset + 6) ? null : cursor.getLong(offset + 6));
     }
    
    @Override
    protected final String updateKeyAfterInsert(BookChapterBean entity, long rowId) {
        return entity.getDurChapterUrl();
    }
    
    @Override
    public String getKey(BookChapterBean entity) {
        if(entity != null) {
            return entity.getDurChapterUrl();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(BookChapterBean entity) {
        return entity.getDurChapterUrl() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}