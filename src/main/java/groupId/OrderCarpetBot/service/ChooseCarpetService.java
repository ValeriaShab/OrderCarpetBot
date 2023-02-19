package groupId.OrderCarpetBot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import groupId.OrderCarpetBot.config.ObjectMapperConfig;
import groupId.OrderCarpetBot.model.Carpet;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ChooseCarpetService {

    private static final ChooseCarpetService INSTANCE = new ChooseCarpetService(
            ObjectMapperConfig.getInstance()
    );
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public List<Carpet> getCarpets(){
        List<Carpet> carpets = new ArrayList<>();
        try (InputStream carpetStream = getClass().getClassLoader()
                .getResourceAsStream("chooseCarpet.json")){
            JsonNode rootNode = objectMapper.readTree(carpetStream);
            if (rootNode.hasNonNull("carpets")){
                JsonNode carpetsNode = rootNode.get("carpets");
                carpets.addAll(List.of(objectMapper.convertValue(carpetsNode, Carpet[].class)));
            }
        }
        return carpets;
    }

    public static ChooseCarpetService getInstance(){
        return INSTANCE;
    }
}
