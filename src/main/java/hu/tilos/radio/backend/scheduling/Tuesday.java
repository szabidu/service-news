package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.NewsBlock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Tuesday implements BaseScheduling {

    @Override
    public List<NewsBlock> createBlocks(LocalDate date, boolean odd) {

        return new BlockBuilder(date)
                .addShort("3perces-5", 5, 0, 6)
                .addShort("3perces-6", 6, 0, 3)
                .addBlock("reggel", LocalTime.of(6, 54), 9 + 3)

                .addShort("3perces-8", 8, 28, 30, 3)

                .addShort("3perces-9-10", 9, 57, 6)
                .addShort("3perces-9-10-havolt", 9, 57, 3)

                .addShort("3perces-11-12", 11, 57, 6)
                .addShort("3perces-11-12-havolt", 11, 57, 3)

                .addShort("3perces-13-14", 13, 57, 6)
                .addShort("3perces-13-14-havolt", 13, 57, 3)

                .addShort("3perces-15", 15, 0, 3)

                .addShort("3perces-16", 16, 28, 30, 3)

                .addShort("3perces-17-18", 17, 57, 6)
                .addShort("3perces-17-18-havolt", 17, 57, 3)

                .addBlock("este", LocalTime.of(19, 48), 9 + 3)

                .addShort("3perces-20-21", 20, 57, 6)
                .addShort("3perces-20-21-havolt", 20, 57, 3)

                .addShort("3perces-22", 22, 0, 3)
                .build();

    }

}