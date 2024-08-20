package fr.epita.assistants.myide.presentation.ExecResponse;

import java.lang.reflect.Array;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class ExecResponse {
    String feature;
    String[] params;
    String path;
}
