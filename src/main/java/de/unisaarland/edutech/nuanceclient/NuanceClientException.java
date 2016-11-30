/*******************************************************************************
 * nuance-client a simple java client for the nuance cloud ASR service.
 * Copyright (C) Tim Steuer (master's thesis 2016)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, US
 *******************************************************************************/
package de.unisaarland.edutech.nuanceclient;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

public class NuanceClientException extends IOException {

	public NuanceClientException(NuanceCredentials creds, Exception ex) {
		super("There was an error in the request with credentials:\n " + creds
				+ "\nSee incapsuled exception below for more details", ex);
	}

	public NuanceClientException(HttpResponse resp, NuanceCredentials creds) {
		super("There was no data in the response for the request with credentials:\n " + creds
				+ "\n see the response for more details:\n----------------" + resp + "-----------\n\n");
	}

	public NuanceClientException(StatusLine statusLine, NuanceCredentials creds, String body) {
		super("Bad response status" + statusLine + " \t with creds: " + creds + "message body" + body);
	}
}
