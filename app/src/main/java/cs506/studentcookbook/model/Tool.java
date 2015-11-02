package cs506.studentcookbook.model;


public class Tool {

    private String name;
    private String description;
    private String imageURL;

    public Tool(String name) {
        this.name = name;
    }

    public Tool() {
        this.name = "";
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    public void setName(String name) {
        if(name == null)
            return;

        name = name.toLowerCase().trim();
        this.name = name;
    }

    public void setDescription(String description) {
        if(name == null)
            return;

        description = description.trim();
        this.description = description;
    }

    public void setImageURL(String imageURL) {
        if(imageURL == null)
            return;

        imageURL = imageURL.trim();
        this.imageURL = imageURL;
    }

    public String toString() {
        return this.name;
    }
}
