
package fr.epita.assistants.myide.presentation.rest.Update;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateRequest {
    public String path;
    public int from;   
    public int to;
    public String content;
}