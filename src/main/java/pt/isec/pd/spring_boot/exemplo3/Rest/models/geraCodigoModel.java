package pt.isec.pd.spring_boot.exemplo3.Rest.models;


import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class geraCodigoModel {

    String descricao, tempoValidade;
}
