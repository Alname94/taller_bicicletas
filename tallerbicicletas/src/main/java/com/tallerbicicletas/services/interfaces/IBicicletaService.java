package com.tallerbicicletas.services.interfaces;

import java.util.List;

import com.tallerbicicletas.models.entities.Bicicleta;

public interface IBicicletaService {

    public List<Bicicleta> getBicicletas();

    public Bicicleta saveBicicleta(Bicicleta bicicleta);

    public void deleteBicicleta(Long id);

    public Bicicleta findBicicleta(Long id);

    public Bicicleta editBicicleta(Bicicleta bicicleta);

    public List<Bicicleta> findByMarcaContainingIgnoreCase(String marca);

    public List<Bicicleta> findByClienteId(Long clienteId);
}
