package pt.isec.pd.spring_boot.exemplo3.Rest.models;

import lombok.*;
import pt.isec.pd.spring_boot.exemplo3.share.events.events;
import pt.isec.pd.spring_boot.exemplo3.share.registo.registo;

import java.util.List;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class consultPresenModel {


    List<events> event;

    List<registo> reg;

}
