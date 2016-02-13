package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.NewsBlock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Thursday implements BaseScheduling {

    @Override
    public List<NewsBlock> createBlocks(LocalDate date, boolean odd) {
        return new BlockBuilder(date)
                .addBlock("reggel", LocalTime.of(5, 0), 12)

                .addShort("3perces-6-7", 6, 57, 6)

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

                .addShort("3perces-19", 19, 30, 3)
                .addBlock("este", LocalTime.of(19, 48), 12)

                .addShort("3perces-20-21", 20, 57, 6)
                .addShort("3perces-20-21-havolt", 20, 57, 3)

                .addShort("3perces-22", 22, 57, 3)
                .build();
    }


}
