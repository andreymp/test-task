package org.fieldwire.challenge.model;

import lombok.Data;

@Data
public class Floorplan {
	private Long id;
	private String projectName;
	private String name;
	private byte[] original;
	private byte[] thumb;
	private byte[] large;
}
