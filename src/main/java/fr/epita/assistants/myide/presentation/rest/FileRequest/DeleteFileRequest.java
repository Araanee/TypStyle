package fr.epita.assistants.myide.presentation.rest.FileRequest;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeleteFileRequest {
    public String path;
}
