package bookmarks.dao;

import bookmarks.domain.Entry;
import bookmarks.domain.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EntryTagDao {
	protected Database db;
	protected TagDao tagDao;

	public EntryTagDao(Database db, TagDao tagDao) {
		this.db = db;
		this.tagDao = tagDao;
	}

	public List<Tag> find(Entry e) throws SQLException {
		PreparedStatement stmt = db.conn.prepareStatement(
			"SELECT tag.id AS id, tag.type AS `type`, tag.name AS `name`" +
				" FROM tag, entry_tag" +
				" WHERE tag.id == entry_tag.tag_id AND entry_tag.entry_id = ?");
		stmt.setInt(1, e.getID());
		ResultSet rs = stmt.executeQuery();
		List<Tag> result = tagDao.read(rs);
		stmt.close();
		return result;
	}

	public void delete(Entry e) throws SQLException {
		PreparedStatement stmt = db.conn.prepareStatement("DELETE FROM entry_tag WHERE entry_id=?");
		stmt.setInt(1, e.getID());
		stmt.execute();
		stmt.close();
	}

	protected void insert(Entry e) throws SQLException {
		PreparedStatement stmt = db.conn.prepareStatement("INSERT INTO entry_tag (entry_id, tag_id) VALUES (?, ?)");
		for (Tag t : e.getTags()) {
			Tag returnT = tagDao.save(t);
			stmt.setInt(1, e.getID());
			stmt.setInt(2, returnT.getID());
			stmt.addBatch();
		}
		stmt.executeBatch();
		stmt.close();
	}

	public void save(Entry e) throws SQLException {
		delete(e);
		insert(e);
	}
}
