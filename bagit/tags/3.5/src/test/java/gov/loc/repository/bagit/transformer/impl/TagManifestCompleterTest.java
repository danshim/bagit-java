package gov.loc.repository.bagit.transformer.impl;

import static org.junit.Assert.*;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.ResourceHelper;

import java.io.File;
import java.io.FileWriter;

public class TagManifestCompleterTest {

	BagFactory bagFactory = new BagFactory();
	TagManifestCompleter completer;	
	Bag bag;
	File testBagFile;
	
	private File createTestBag() throws Exception {
		File sourceBagDir = ResourceHelper.getFile("bags/v0_96/bag");
		File testBagDir = new File(sourceBagDir.getParentFile(), "test_bag");
		if (testBagDir.exists()) {
			FileUtils.forceDelete(testBagDir);
		}
		FileUtils.copyDirectory(sourceBagDir, testBagDir);
		return testBagDir;
	}

	
	@Before
	public void setup() throws Exception {
		completer = new TagManifestCompleter(this.bagFactory);
		this.testBagFile = this.createTestBag();
		this.bag = this.bagFactory.createBag(testBagFile);
		assertTrue(this.bag.verifyValid().isSuccess());
	}

	@Test
	public void testComplete() throws Exception {
		Bag newBag = completer.complete(bag);
		assertTrue(newBag.verifyValid().isSuccess());
		//Add a tag
		File test1File = new File(this.testBagFile, "tag.txt");
		assertFalse(test1File.exists());
		FileWriter writer = new FileWriter(test1File);
		writer.write("tag");
		writer.close();
		newBag.addFileAsTag(test1File);
		
		Bag newBag2 = completer.complete(newBag);
		assertTrue(newBag2.verifyValid().isSuccess());
		
		//Change a tag
		writer = new FileWriter(test1File);
		writer.write("xtag");
		writer.close();
		
		Bag newBag3 = completer.complete(newBag2);
		assertTrue(newBag3.verifyValid().isSuccess());

		//Remove a tag
		test1File.delete();
		newBag3.removeBagFile("tag.txt");
		
		Bag newBag4 = completer.complete(newBag3);
		assertTrue(newBag4.verifyValid().isSuccess());

	}
	
}