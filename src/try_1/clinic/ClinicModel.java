package try_1.clinic;

import try_1.core_1.Process;
import try_1.core_1.*;

import java.util.ArrayList;

public class ClinicModel extends Model {
    public ClinicModel(Element... elements) {
        super(elements);
    }

    private double getLaboratoryArrivalInterval() {
        for (var element : elements) {
            if (element.getName().equals("Laboratory Transfer")) {
                return ((Process) element).getMeanLeaveInterval();
            }
        }
        return 0.0;
    }

    @Override
    public void printResult() {
        System.out.println("\n-------------RESULTS-------------");
        printPatientInfo();
        System.out.println("\n-----------STATISTICS------------");
        System.out.println("Mean time in system (processed): " + getMeanTimeInSystem());
        System.out.println("Mean laboratory arrival interval: " + getLaboratoryArrivalInterval());
    }

    private void printPatientInfo() {
        System.out.println("\n-------------PATIENTS------------");
        for (var element : elements) {
            if (element instanceof Dispose d) {
                var patients = d.getProcessedJobs();
                for (var patient : patients) {
                    System.out.println("Patient " + patient.getId() +
                            " type " + ((Patient) patient).getType() +
                            " time in " + patient.getTimeIn() +
                            " time out " + patient.getTimeOut() +
                            " time in system " + (patient.getTimeOut() - patient.getTimeIn()));
                }
            }
        }
    }

    private double getMeanTimeInSystem() {
        var patients = new ArrayList<Job>();
        for (var element : elements) {
            if (element instanceof Dispose d) {
                patients.addAll(d.getProcessedJobs());
            }
        }
        var sum = 0.0;
        for (var patient : patients) {
            sum += patient.getTimeOut() - patient.getTimeIn();
        }
        return sum / patients.size();
    }
}