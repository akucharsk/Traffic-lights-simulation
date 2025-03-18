import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.example.location.Direction;
import org.example.location.Junction;
import org.example.json.JunctionSerializer;
import org.example.location.Vehicle;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class TestJunctionSerializer {

    private ObjectMapper mapper = new ObjectMapper();

    private void setup() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Junction.class, new JunctionSerializer(Junction.class));
        mapper.registerModule(module);
    }

    @Test
    public void test() throws JsonProcessingException {
        setup();
        Junction junction = new Junction();
        junction.addVehicle(new Vehicle("1", Direction.NORTH, Direction.SOUTH));
        junction.addVehicle(new Vehicle("2", Direction.EAST, Direction.SOUTH));
        junction.addVehicle(new Vehicle("3", Direction.WEST, Direction.SOUTH));
        junction.addVehicle(new Vehicle("4", Direction.NORTH, Direction.EAST));
        junction.addVehicle(new Vehicle("5", Direction.SOUTH, Direction.WEST));

    }
}
