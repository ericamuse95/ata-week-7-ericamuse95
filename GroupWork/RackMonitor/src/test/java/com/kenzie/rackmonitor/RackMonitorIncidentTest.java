package com.kenzie.rackmonitor;

import com.kenzie.rackmonitor.*;
import com.kenzie.rackmonitor.clients.warranty.Warranty;
import com.kenzie.rackmonitor.clients.warranty.WarrantyClient;
import com.kenzie.rackmonitor.clients.warranty.WarrantyNotFoundException;
import com.kenzie.rackmonitor.clients.wingnut.WingnutClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RackMonitorIncidentTest {

    @InjectMocks
    RackMonitor rackMonitor;
    @Mock
    WingnutClient wingnutClient;
    @Mock
    WarrantyClient warrantyClient;
    @Mock
    Rack rack1;
    Server unhealthyServer = new Server("TEST0001");
    Server shakyServer = new Server("TEST0067");

    Map<Server, Integer> rack1ServerUnits;

    @BeforeEach
    void setUp() throws NoSuchServerException, WarrantyNotFoundException {

        MockitoAnnotations.openMocks(this);

        //rack1 = new Rack("RACK01", rack1ServerUnits);
        rack1ServerUnits = new HashMap<>();
        rack1ServerUnits.put(unhealthyServer, 1);

        rackMonitor = new RackMonitor(new HashSet<>(Arrays.asList(rack1)),
                wingnutClient, warrantyClient, 0.9D, 0.8D);
    }

    @Test
    public void getIncidents_withOneUnhealthyServer_createsOneReplaceIncident() throws Exception {
        // GIVEN
        // The rack is set up with a single unhealthy server
        Map<Server, Double> serverHealthMap = new HashMap<>();
        serverHealthMap.put(unhealthyServer,0.7);
        // We've reported the unhealthy server to Wingnut
        when(rack1.getHealth()).thenReturn(serverHealthMap);
        when(rack1.getUnitForServer(unhealthyServer)).thenReturn(1);
        when(warrantyClient.getWarrantyForServer(unhealthyServer)).thenReturn(new Warranty("123"));
        rackMonitor.monitorRacks();

        // WHEN
        Set<HealthIncident> actualIncidents = rackMonitor.getIncidents();

        // THEN
        HealthIncident expected =
            new HealthIncident(unhealthyServer, rack1, 1, RequestAction.REPLACE);
        assertTrue(actualIncidents.contains(expected),
            "Monitoring an unhealthy server should record a REPLACE incident!");
        verify(rack1, times(1)).getHealth();
    }

    @Test
    public void getIncidents_withOneShakyServer_createsOneInspectIncident() throws Exception {
        // GIVEN
        // The rack is set up with a single shaky server
        rack1ServerUnits = new HashMap<>();
        rack1ServerUnits.put(shakyServer, 1);
        rack1 = new Rack("RACK01", rack1ServerUnits);
        rackMonitor = new RackMonitor(new HashSet<>(Arrays.asList(rack1)),
            wingnutClient, warrantyClient, 0.9D, 0.8D);
        // We've reported the shaky server to Wingnut
        rackMonitor.monitorRacks();

        // WHEN
        Set<HealthIncident> actualIncidents = rackMonitor.getIncidents();

        // THEN
        HealthIncident expected =
            new HealthIncident(shakyServer, rack1, 1, RequestAction.INSPECT);
        assertTrue(actualIncidents.contains(expected),
            "Monitoring a shaky server should record an INSPECT incident!");
    }

    @Test
    public void getIncidents_withOneHealthyServer_createsNoIncidents() throws Exception {
        // GIVEN
        // monitorRacks() will find only healthy servers

        // WHEN
        Set<HealthIncident> actualIncidents = rackMonitor.getIncidents();

        // THEN
        assertEquals(0, actualIncidents.size(),
            "Monitoring a healthy server should record no incidents!");
    }

    @Test
    public void monitorRacks_withOneUnhealthyServer_replacesServer() throws Exception {
        // GIVEN
        // The rack is set up with a single unhealthy server

        // WHEN
        rackMonitor.monitorRacks();

        // THEN
        // There were no exceptions
        // No way to tell we called the warrantyClient for the server's Warranty
        // No way to tell we called Wingnut to replace the server
    }

    @Test
    public void monitorRacks_withUnwarrantiedServer_throwsServerException() throws Exception {
        // GIVEN
        Server noWarrantyServer = new Server("TEST0052");
        rack1ServerUnits = new HashMap<>();
        rack1ServerUnits.put(noWarrantyServer, 1);
        rack1 = new Rack("RACK01", rack1ServerUnits);
        rackMonitor = new RackMonitor(new HashSet<>(Arrays.asList(rack1)),
            wingnutClient, warrantyClient, 0.9D, 0.8D);

        when(warrantyClient.getWarrantyForServer(noWarrantyServer)).thenThrow(WarrantyNotFoundException.class);

        // WHEN and THEN
        assertThrows(RackMonitorException.class,
            () -> rackMonitor.monitorRacks(),
            "Monitoring a server with no warranty should throw exception!");
    }
    @Test
    public void setRackMonitor_duplicateServerEvents_createsOnlyOneServerHealthIncident() throws Exception{
        //GIVEN
        Map<Server, Double> health = new HashMap<>();
        health.put(unhealthyServer,0.7);
        when(rack1.getHealth()).thenReturn(health);
        when(rack1.getUnitForServer(unhealthyServer)).thenReturn(1);
        Warranty warranty = new Warranty((String) unhealthyServer.getServerId());
        when(warrantyClient.getWarrantyForServer(unhealthyServer)).thenReturn(warranty);

        //WHEN
        rackMonitor.monitorRacks();
        rackMonitor.monitorRacks();
        Set<HealthIncident> actualIncidents = rackMonitor.getIncidents();

        //THEN
        verify(wingnutClient, times(1)).requestReplacement(rack1,1,warranty);
        assertEquals(1, actualIncidents.size());

    }

}
