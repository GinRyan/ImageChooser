package org.ryan.picur;

public class ImageEntity {
	public int position;
	public String fileLocalPath;

	/**
	 * @return the position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * @return the fileLocalPath
	 */
	public String getFileLocalPath() {
		return fileLocalPath;
	}

	/**
	 * @param fileLocalPath
	 *            the fileLocalPath to set
	 */
	public void setFileLocalPath(String fileLocalPath) {
		this.fileLocalPath = fileLocalPath;
	}

	/**
	 * @param position
	 * @param fileLocalPath
	 */
	public ImageEntity(int position, String fileLocalPath) {
		super();
		this.position = position;
		this.fileLocalPath = fileLocalPath;
	}

}
