package try_1;


import try_1.clinic.*;
import try_1.core_1.Process;
import try_1.core_1.*;

public class Main {
    public static void main(String[] args) {
        clinic();
    }

    public static void clinic() {
        final int[] patientTypes = {1, 2, 3};
        final double[] patientFrequencies = {0.5, 0.1, 0.4};
        final double[] patientDelays = {15, 40, 30};

        var create = new PatientCreate("Patient Creator", 15);
        var registration = new RegistrationProcess("Registration", 15, 2);
        var wardsTransfer = new Process("Wards Transfer", 3, 8, 3);
        var laboratoryTransfer = new Process("Laboratory Transfer", 2, 5, 100);
        var laboratoryRegistration = new Process("Laboratory Registration", 4.5, 3, 1);
        var laboratoryAnalysis = new TypeModifyingProcess("Laboratory Analysis", 4, 2, 2);
        var registrationTransfer = new Process("Registration Transfer", 2, 5, 100);

        var wardsDispose = new Dispose("Dispose [Type 1 & 2]");
        var laboratoryDispose = new Dispose("Dispose [Type 3]");


        create.setPatientTypedFrequencies(patientTypes, patientFrequencies);
        registration.setPatientTypedDelays(patientTypes, patientDelays);
        registration.setPrioritizedPatientType(1);
        laboratoryAnalysis.setTypeModifyingMap(
                new int[]{2},
                new int[]{1}
        );

        create.setDistribution(Distribution.EXPONENTIAL);
        registration.setDistribution(Distribution.EXPONENTIAL);
        wardsTransfer.setDistribution(Distribution.UNIFORM);
        laboratoryTransfer.setDistribution(Distribution.UNIFORM);
        laboratoryRegistration.setDistribution(Distribution.ERLANG);
        laboratoryAnalysis.setDistribution(Distribution.ERLANG);
        registrationTransfer.setDistribution(Distribution.UNIFORM);

        create.addRoutes(
                new Route(registration)
        );
        registration.addRoutes(
                new Route(wardsTransfer, 0.5, 1, (Job job) -> ((Patient) job).getType() != 1),
                new Route(laboratoryTransfer, 0.5, 0)
        );
        registration.setRouting(Routing.BY_PRIORITY);
        wardsTransfer.addRoutes(
                new Route(wardsDispose)
        );
        laboratoryTransfer.addRoutes(
                new Route(laboratoryRegistration)
        );
        laboratoryRegistration.addRoutes(
                new Route(laboratoryAnalysis)
        );
        laboratoryAnalysis.addRoutes(
                new Route(laboratoryDispose, 0.5, 1, (Job job) -> ((Patient) job).getType() != 3),
                new Route(registrationTransfer, 0.5, 0)
        );
        laboratoryAnalysis.setRouting(Routing.BY_PRIORITY);
        registrationTransfer.addRoutes(
                new Route(registration)
        );

        var model = new ClinicModel(create, registration, wardsTransfer, laboratoryTransfer, laboratoryRegistration,
                laboratoryAnalysis, registrationTransfer, wardsDispose, laboratoryDispose);
        model.simulate(1000);
    }
}