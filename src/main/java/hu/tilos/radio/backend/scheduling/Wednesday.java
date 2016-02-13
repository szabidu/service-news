package hu.tilos.radio.backend.scheduling;

import hu.tilos.radio.backend.NewsBlock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Wednesday implements BaseScheduling {

    @Override
    public List<NewsBlock> createBlocks(LocalDate date, boolean odd) {
        return new BlockBuilder(date)
                .addBlock("reggel", LocalTime.of(5, 0), 12)

                .addShort("3perces-6-7", 6, 54, 9, "2*3 perc + 3 a 12-ből")

                .addShort("3perces-8", 8, 28, 30, 3)

                .addShort("3perces-9-10", 9, 57, 6)
                .addShort("3perces-9-10-havolt", 9, 57, 3)

                .addShort("3perces-11-12", 11, 57, 6)
                .addShort("3perces-11-12-havolt", 11, 57, 3)

                .addShort("3perces-13", 13, 0, 3)

                .addShort("3perces-14-15", 14, 57, 6)
                .addShort("3perces-14-15-havolt", 14, 57, 3)


                .addShort("3perces-16", 16, 0, 3)

                .addShort("3perces-17", 17, 0, 3)
                .addShort("3perces-18", 18, 28, 30, 3)

                .addBlock("este", LocalTime.of(19, 48), 9 + 3)

                .addShort("3perces-20", 20, 57, 3)

                .addShort("3perces-21-22", 21, 57, 6)
                .addShort("3perces-21-22-havolt", 22, 0, 3)
                .build();
    }

}
