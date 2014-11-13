package org.cloudfoundry.client.lib.io;

import java.util.TimeZone;
import java.util.zip.ZipEntry;

public class UtcAdjustedZipEntry extends ZipEntry {
	private final int utcOffset = TimeZone.getDefault().getOffset(System.currentTimeMillis());

	public UtcAdjustedZipEntry(String name) {
		super(name);
		setTime(System.currentTimeMillis() - utcOffset);
	}

	public UtcAdjustedZipEntry(ZipEntry e) {
		super(e);
		setTime(System.currentTimeMillis() - utcOffset);
	}
}
