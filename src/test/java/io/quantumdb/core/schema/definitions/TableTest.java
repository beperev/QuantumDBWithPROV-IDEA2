package io.quantumdb.core.schema.definitions;

import static io.quantumdb.core.schema.definitions.Column.Hint.AUTO_INCREMENT;
import static io.quantumdb.core.schema.definitions.Column.Hint.IDENTITY;
import static io.quantumdb.core.schema.definitions.Column.Hint.NOT_NULL;
import static io.quantumdb.core.schema.definitions.GenericColumnTypes.bool;
import static io.quantumdb.core.schema.definitions.GenericColumnTypes.int8;
import static io.quantumdb.core.schema.definitions.GenericColumnTypes.varchar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.junit.Test;

public class TableTest {

	@Test
	public void testCreatingTable() {
		Table table = new Table("users");

		assertEquals("users", table.getName());
		assertTrue(table.getColumns().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatingTableWithNullForCatalogName() {
		new Table(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreatingTableWithEmptyStringForCatalogName() {
		new Table("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddingNullColumnToTable() {
		new Table("public").addColumn(null);
	}

	@Test
	public void testAddingColumnToTable() {
		new Table("users").addColumn(new Column("id", int8(), IDENTITY, AUTO_INCREMENT));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatAddingColumnWithAnAlreadyTakenNameToTableThrowsException() {
		new Table("users")
				.addColumn(new Column("id", int8(), IDENTITY, AUTO_INCREMENT))
				.addColumn(new Column("id", varchar(255)));
	}

	@Test
	public void testAddingMutipleColumnsToTable() {
		new Table("users").addColumns(Lists.newArrayList(
				new Column("id", int8(), IDENTITY, AUTO_INCREMENT),
				new Column("name", varchar(255), NOT_NULL)
		));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatAddColumnsMethodThrowsExceptionWhenInputIsNull() {
		new Table("users").addColumns(null);
	}

	@Test
	public void testThatContainsColumnMethodReturnsTrueWhenColumnExists() {
		Table table = new Table("users")
				.addColumn(new Column("id", int8(), IDENTITY, AUTO_INCREMENT));

		assertTrue(table.containsColumn("id"));
	}

	@Test
	public void testThatContainsColumnMethodReturnsFalseWhenColumnDoesNotExist() {
		Table table = new Table("users");

		assertFalse(table.containsColumn("id"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatContainsColumnMethodThrowsExceptionOnNullInput() {
		Table table = new Table("users");
		table.containsColumn(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatContainsColumnMethodThrowsExceptionOnEmptyStringInput() {
		Table table = new Table("users");
		table.containsColumn("");
	}

	@Test
	public void testThatGetColumnMethodReturnsColumnWhenItExists() {
		Column column = new Column("id", int8(), IDENTITY, AUTO_INCREMENT);
		Table table = new Table("users").addColumn(column);

		assertEquals(column, table.getColumn("id"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatGetTableMethodThrowsExceptionWhenTableDoesNotExist() {
		Table table = new Table("users");
		table.getColumn("id");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatGetColumnMethodThrowsExceptionOnNullInput() {
		Table table = new Table("users");
		table.getColumn(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatGetColumnMethodThrowsExceptionOnEmptyStringInput() {
		Table table = new Table("users");
		table.getColumn("");
	}

	@Test
	public void testThatGetIdentityColumnMethodReturnsOneColumnForTableWithoutIdentityColumns() {
		Table table = new Table("users")
				.addColumn(new Column("name", varchar(255), NOT_NULL));

		assertTrue(table.getIdentityColumns().isEmpty());
	}

	@Test
	public void testThatGetIdentityColumnMethodReturnsOneColumnForTableWithSinglePrimaryKey() {
		Column idColumn = new Column("id", int8(), IDENTITY, AUTO_INCREMENT);

		Table table = new Table("users")
				.addColumn(idColumn)
				.addColumn(new Column("name", varchar(255), NOT_NULL));

		assertEquals(Lists.newArrayList(idColumn), table.getIdentityColumns());
	}

	@Test
	public void testThatGetIdentityColumnMethodReturnsOneColumnForTableWithCompositeKey() {
		Column id1Column = new Column("left_id", int8(), IDENTITY, AUTO_INCREMENT);
		Column id2Column = new Column("right_id", int8(), IDENTITY, AUTO_INCREMENT);

		Table table = new Table("link_table")
				.addColumn(id1Column)
				.addColumn(id2Column)
				.addColumn(new Column("some_property", bool(), NOT_NULL));

		assertEquals(Lists.newArrayList(id1Column, id2Column), table.getIdentityColumns());
	}

	@Test
	public void testThatRemoveColumnMethodRemovesColumnWhenItExists() {
		Column column = new Column("id", int8(), IDENTITY, AUTO_INCREMENT);
		Table table = new Table("users").addColumn(column);

		Column removedColumn = table.removeColumn("id");
		assertEquals(column, removedColumn);
		assertFalse(table.containsColumn("id"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatRemoveColumnMethodThrowsExceptionWhenColumnDoesNotExist() {
		Table table = new Table("users");
		table.removeColumn("id");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatRemoveColumnMethodThrowsExceptionOnNullInput() {
		Table table = new Table("users");
		table.removeColumn(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatRemoveColumnMethodThrowsExceptionOnEmptyStringInput() {
		Table table = new Table("users");
		table.removeColumn("");
	}

	@Test
	public void testThatRenamingColumnIsReflectedInTable() {
		Column column = new Column("id", int8(), IDENTITY, AUTO_INCREMENT);
		Table table = new Table("users").addColumn(column);

		column.rename("uuid");

		assertFalse(table.containsColumn("id"));
		assertTrue(table.containsColumn("uuid"));
		assertEquals(column, table.getColumn("uuid"));
	}

	@Test
	public void testRenamingTable() {
		Table table = new Table("users");
		table.rename("other_name");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatRenamingTableToNullThrowsException() {
		Column column = new Column("id", int8(), IDENTITY, AUTO_INCREMENT);
		Table table = new Table("users").addColumn(column);
		table.rename(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testThatRenamingColumnToEmptyStringThrowsException() {
		Column column = new Column("id", int8(), IDENTITY, AUTO_INCREMENT);
		Table table = new Table("users").addColumn(column);
		table.rename("");
	}

	@Test
	public void testThatCopyMethodReturnsCopy() {
		Column column = new Column("id", int8(), IDENTITY, AUTO_INCREMENT);
		Table table = new Table("users").addColumn(column);

		Table copy = table.copy();

		assertEquals(table, copy);
		assertFalse(table == copy);
	}

	@Test
	public void toStringReturnsSomething() {
		Table table = new Table("users")
				.addColumn(new Column("id", int8(), IDENTITY, AUTO_INCREMENT));

		assertFalse(Strings.isNullOrEmpty(table.toString()));
	}

}