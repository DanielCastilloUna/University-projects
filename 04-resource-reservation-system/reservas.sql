-- ============================================================
--  Sistema de Reservas - Script de base de datos (MySQL)
--  EIF206 Programacion 3 - Proyecto 2
--
--  Uso:  mysql -u root -p < reservas.sql
--  OJO:  este script BORRA y vuelve a crear la base de datos "Reservas".
-- ============================================================

DROP DATABASE IF EXISTS Reservas;
CREATE DATABASE Reservas CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE Reservas;

create table Administrador (
    id    varchar(20) not null,
    clave varchar(50) not null,
    Primary Key (id)
);

create table Funcionario (
    id       varchar(20)  not null,
    clave    varchar(50)  not null,
    nombre   varchar(100) not null,
    telefono varchar(30)  not null,
    Primary Key (id)
);

create table CategoriaRecurso (
    id          varchar(20)  not null,
    descripcion varchar(100) not null,
    Primary Key (id)
);

create table Recurso (
    id          varchar(20)  not null,
    categoria   varchar(20)  not null,
    descripcion varchar(150) not null,
    Primary Key (id)
);

create table Reserva (
    id          varchar(20)  not null,
    funcionario varchar(20)  not null,
    actividad   varchar(200) not null,
    fecha       date         not null,
    horaInicio  time         not null,
    horaFin     time         not null,
    estado      varchar(15)  not null,
    Primary Key (id)
);

-- Categorias que el funcionario pidio en la reserva (relacion muchos a muchos)
create table ReservaCategoria (
    reserva   varchar(20) not null,
    categoria varchar(20) not null,
    Primary Key (reserva, categoria)
);

-- Recursos que el sistema asigno a la reserva (relacion muchos a muchos)
create table ReservaRecurso (
    reserva varchar(20) not null,
    recurso varchar(20) not null,
    Primary Key (reserva, recurso)
);

ALTER TABLE Recurso          ADD Foreign Key (categoria)   REFERENCES CategoriaRecurso(id);
ALTER TABLE Reserva          ADD Foreign Key (funcionario) REFERENCES Funcionario(id);
ALTER TABLE ReservaCategoria ADD Foreign Key (reserva)     REFERENCES Reserva(id) ON DELETE CASCADE;
ALTER TABLE ReservaCategoria ADD Foreign Key (categoria)   REFERENCES CategoriaRecurso(id);
ALTER TABLE ReservaRecurso   ADD Foreign Key (reserva)     REFERENCES Reserva(id) ON DELETE CASCADE;
ALTER TABLE ReservaRecurso   ADD Foreign Key (recurso)     REFERENCES Recurso(id);

create index idx_reserva_fecha on Reserva(fecha);

-- ============================================================
--  Datos iniciales (migrados desde data.xml del Proyecto 1)
-- ============================================================

-- Administrador
insert into Administrador (id, clave) values('admin','admin');

-- Funcionarios (la clave inicial es igual al id)
insert into Funcionario (id, clave, nombre, telefono) values('222','222','Carlos Mendez','8888-2222');
insert into Funcionario (id, clave, nombre, telefono) values('333','333','Laura Jimenez','8888-3333');
insert into Funcionario (id, clave, nombre, telefono) values('444','444','Juan','44444');
insert into Funcionario (id, clave, nombre, telefono) values('111','111','Ana','111-111');

-- Categorias de recurso
insert into CategoriaRecurso (id, descripcion) values('CAT-000001','Sala de Juntas');
insert into CategoriaRecurso (id, descripcion) values('CAT-000002','Equipo Audiovisual');
insert into CategoriaRecurso (id, descripcion) values('CAT-000003','Vehiculo');
insert into CategoriaRecurso (id, descripcion) values('CAT-000004','Laptop');

-- Recursos
insert into Recurso (id, categoria, descripcion) values('R-001','CAT-000001','Sala de Juntas Principal');
insert into Recurso (id, categoria, descripcion) values('R-002','CAT-000001','Sala de Juntas Secundaria');
insert into Recurso (id, categoria, descripcion) values('R-003','CAT-000002','Proyector Epson X200');
insert into Recurso (id, categoria, descripcion) values('R-004','CAT-000002','Pantalla Portatil');
insert into Recurso (id, categoria, descripcion) values('R-005','CAT-000003','Vehiculo Institucional Placa 123');
insert into Recurso (id, categoria, descripcion) values('R-006','CAT-000004','Laptop Dell Latitude 01');
insert into Recurso (id, categoria, descripcion) values('R-007','CAT-000004','Laptop Dell Latitude 02');

-- Reservas
insert into Reserva (id, funcionario, actividad, fecha, horaInicio, horaFin, estado) values('RES-0001','111','Reunion de planificacion semanal','2026-09-08','09:00','10:00','CONFIRMADA');
insert into Reserva (id, funcionario, actividad, fecha, horaInicio, horaFin, estado) values('RES-0002','222','Presentacion de resultados trimestrales','2026-09-08','14:00','16:00','CONFIRMADA');
insert into Reserva (id, funcionario, actividad, fecha, horaInicio, horaFin, estado) values('RES-0003','333','Visita a proveedor en Heredia','2026-09-09','08:00','12:00','CONFIRMADA');
insert into Reserva (id, funcionario, actividad, fecha, horaInicio, horaFin, estado) values('RES-0004','111','Capacitacion de personal nuevo','2026-09-10','10:00','12:00','CONFIRMADA');
insert into Reserva (id, funcionario, actividad, fecha, horaInicio, horaFin, estado) values('RES-0005','222','Reunion cancelada con cliente','2026-09-11','15:00','16:00','CANCELADA');
insert into Reserva (id, funcionario, actividad, fecha, horaInicio, horaFin, estado) values('RES-0006','333','Entrevista a candidato nuevo','2026-09-15','09:00','10:00','CONFIRMADA');
insert into Reserva (id, funcionario, actividad, fecha, horaInicio, horaFin, estado) values('RES-0007','111','Mantenimiento de equipo audiovisual','2026-09-16','13:00','14:00','CONFIRMADA');
insert into Reserva (id, funcionario, actividad, fecha, horaInicio, horaFin, estado) values('RES-0008','222','Entrega de materiales a sucursal','2026-09-17','08:00','11:00','CONFIRMADA');
insert into Reserva (id, funcionario, actividad, fecha, horaInicio, horaFin, estado) values('RES-0009','111','reunion','2026-09-19','09:00','12:00','CONFIRMADA');
insert into Reserva (id, funcionario, actividad, fecha, horaInicio, horaFin, estado) values('RES-0010','111','reunion','2026-09-19','08:00','12:00','CONFIRMADA');
insert into Reserva (id, funcionario, actividad, fecha, horaInicio, horaFin, estado) values('RES-0011','111','Reserva en sala de juntas','2026-09-20','08:00','10:00','CONFIRMADA');

-- Categorias pedidas en cada reserva
insert into ReservaCategoria (reserva, categoria) values('RES-0001','CAT-000001');
insert into ReservaCategoria (reserva, categoria) values('RES-0002','CAT-000001');
insert into ReservaCategoria (reserva, categoria) values('RES-0002','CAT-000002');
insert into ReservaCategoria (reserva, categoria) values('RES-0003','CAT-000003');
insert into ReservaCategoria (reserva, categoria) values('RES-0004','CAT-000004');
insert into ReservaCategoria (reserva, categoria) values('RES-0005','CAT-000001');
insert into ReservaCategoria (reserva, categoria) values('RES-0006','CAT-000001');
insert into ReservaCategoria (reserva, categoria) values('RES-0007','CAT-000002');
insert into ReservaCategoria (reserva, categoria) values('RES-0008','CAT-000003');
insert into ReservaCategoria (reserva, categoria) values('RES-0009','CAT-000004');
insert into ReservaCategoria (reserva, categoria) values('RES-0010','CAT-000003');
insert into ReservaCategoria (reserva, categoria) values('RES-0011','CAT-000001');

-- Recursos asignados a cada reserva
insert into ReservaRecurso (reserva, recurso) values('RES-0001','R-001');
insert into ReservaRecurso (reserva, recurso) values('RES-0002','R-002');
insert into ReservaRecurso (reserva, recurso) values('RES-0002','R-003');
insert into ReservaRecurso (reserva, recurso) values('RES-0003','R-005');
insert into ReservaRecurso (reserva, recurso) values('RES-0004','R-006');
insert into ReservaRecurso (reserva, recurso) values('RES-0005','R-001');
insert into ReservaRecurso (reserva, recurso) values('RES-0006','R-001');
insert into ReservaRecurso (reserva, recurso) values('RES-0007','R-004');
insert into ReservaRecurso (reserva, recurso) values('RES-0008','R-005');
insert into ReservaRecurso (reserva, recurso) values('RES-0009','R-006');
insert into ReservaRecurso (reserva, recurso) values('RES-0010','R-005');
insert into ReservaRecurso (reserva, recurso) values('RES-0011','R-001');
