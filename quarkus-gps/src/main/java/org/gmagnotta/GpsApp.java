package org.gmagnotta;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.taimos.gpsd4java.api.ObjectListener;
import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.backend.ResultParser;
import de.taimos.gpsd4java.types.ATTObject;
import de.taimos.gpsd4java.types.DeviceObject;
import de.taimos.gpsd4java.types.DevicesObject;
import de.taimos.gpsd4java.types.SATObject;
import de.taimos.gpsd4java.types.SKYObject;
import de.taimos.gpsd4java.types.TPVObject;
import de.taimos.gpsd4java.types.subframes.SUBFRAMEObject;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;

public class GpsApp implements QuarkusApplication {
    
    @Inject
    Coordinates coordinates;

    @ConfigProperty(name = "gpsdserver")
    String gpsdserver;

    @ConfigProperty(name = "gpsdport")
    int gpsdport;

    @Override
    public int run(String... args) throws Exception {

        final GPSdEndpoint ep = new GPSdEndpoint(gpsdserver, gpsdport, new ResultParser());

        ep.addListener(new ObjectListener() {

            @Override
            public void handleTPV(final TPVObject tpv) {
                System.out.println("TPV: " + tpv);
                coordinates.setAltitude(tpv.getAltitude());
                coordinates.setLatitude(tpv.getLatitude());
                coordinates.setLongitude(tpv.getLongitude());
            }

            @Override
            public void handleSKY(final SKYObject sky) {
                System.out.println("SKY: " + sky);
                for (final SATObject sat : sky.getSatellites()) {
                    System.out.println("  SAT: " + sat);
                }
            }

            @Override
            public void handleSUBFRAME(final SUBFRAMEObject subframe) {
                System.out.println("SUBFRAME: " + subframe);
            }

            @Override
            public void handleATT(final ATTObject att) {
                System.out.println("ATT: " + att);
            }

            @Override
            public void handleDevice(final DeviceObject device) {
                System.out.println("Device: " + device);
            }

            @Override
            public void handleDevices(final DevicesObject devices) {
                for (final DeviceObject d : devices.getDevices()) {
                    System.out.println("Device: " + d);
                }
            }
        });

        ep.start();

        System.out.println("Version: " + ep.version());

        System.out.println("Watch: " + ep.watch(true, true));

        System.out.println("Poll: " + ep.poll());

        Quarkus.waitForExit();

        return 0;
    }

}
