package org.cloudfoundry.client.lib.rest;

import org.cloudfoundry.client.lib.domain.UploadApplicationPayload;
import org.springframework.http.converter.FormHttpMessageConverter;

public class CloudFoundryFormHttpMessageConverter extends FormHttpMessageConverter {
	@Override
	protected String getFilename(Object part) {
		if (part instanceof UploadApplicationPayload) {
			return ((UploadApplicationPayload) part).getArchive().getFilename();
		}
		return super.getFilename(part);
	}
}
