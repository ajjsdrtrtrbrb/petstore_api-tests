package pac1.Model;

public class Activity {
    /*
    {
  "id": 0,
  "title": "string",
  "dueDate": "2025-07-25T13:37:17.092Z",
  "completed": true
}
     */
    private int id;
    private String title;
    private String dueDate;
    private boolean completed;

    public Activity(int id, String title, String dueDate, boolean completed) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.completed = completed;
    }
    public Activity(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
