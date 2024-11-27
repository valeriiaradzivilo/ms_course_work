package try_1.clinic;

import try_1.core_1.Job;

public class Patient extends Job {
    int type;

    public Patient(double timeIn, int type) {
        super(timeIn);
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}