package pt.isec.pd.spring_boot.exemplo3.Rest.models;
public class LoremConfig {
    private String type;
    private Integer length;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}