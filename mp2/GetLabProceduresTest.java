package edu.ncsu.csc.itrust.unit.dao.labprocedure;

import java.util.List;

import junit.framework.TestCase;
import edu.ncsu.csc.itrust.beans.LabProcedureBean;
import edu.ncsu.csc.itrust.dao.mysql.LabProcedureDAO;
import edu.ncsu.csc.itrust.exception.DBException;
import edu.ncsu.csc.itrust.unit.datagenerators.TestDataGenerator;
import edu.ncsu.csc.itrust.unit.testutils.TestDAOFactory;

public class GetLabProceduresTest extends TestCase {
	private LabProcedureDAO lpDAO = TestDAOFactory.getTestInstance().getLabProcedureDAO();
    private TestDataGenerator gen;

	@Override
	protected void setUp() throws Exception {
		gen = new TestDataGenerator();
		gen.clearAllTables();
		gen.officeVisit7();
	}

	public void testGetLabProcedures() throws Exception {
		List<LabProcedureBean> procedures = lpDAO.getLabProcedures(9000000005L, 1L);
		assertEquals(1, procedures.size());
		assertEquals("45335-7", procedures.get(0).getLoinc());
	}

	public void testFailGetLabProcedures() throws Exception {
		try {
			lpDAO.getLabProcedures(0L, 0L);
			fail("Exception should have been thrown");
		} catch (DBException e) {
			assertEquals("HCP id cannot be null", e.getExtendedMessage());
		}
	}
}
