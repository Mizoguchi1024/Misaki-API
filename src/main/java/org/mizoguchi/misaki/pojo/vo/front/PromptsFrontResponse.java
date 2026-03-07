package org.mizoguchi.misaki.pojo.vo.front;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptsFrontResponse {
    private List<String> prompts;
}
