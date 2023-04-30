package org.tool.csvtodb.common.dto;

public class MemberInfoDto {
    private String id; // (1)

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBikou() {
		return bikou;
	}

	public void setBikou(String bikou) {
		this.bikou = bikou;
	}

	private String name; // original1

    private String type; // (2)

    private String status; // (3)

    private int point; // (4)

    private String bikou; // original2

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
