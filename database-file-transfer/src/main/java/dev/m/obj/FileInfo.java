package dev.m.obj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {
    private String fileParent;
    private List<File> fileChildren;
    private int numChildren;
    private boolean state;
}
