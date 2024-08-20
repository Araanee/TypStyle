
package fr.epita.assistants.myide.presentation.rest.Move;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MoveRequest {
    public String src;
    public String dst;
}