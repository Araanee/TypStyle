package fr.epita.assistants.myide.presentation.rest.Feature;

import java.util.ArrayList; 
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExecFeatureRequest {
    String feature;
    ArrayList<String> params;
    String project;
    
}
