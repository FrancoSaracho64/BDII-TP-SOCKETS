--
-- Creación de la tabla empleado
--
CREATE TABLE public.empleado (
    ide integer NOT NULL,
    apenom character varying,
    mail character varying,
    telefono character varying,
    direccion character varying
);

--
-- Creación de la tabla lenguaje
--
CREATE TABLE public.lenguaje (
    nombre character varying NOT NULL
);

--
-- Creación de la tabla sabe (Tabla de relación N:M)
--
CREATE TABLE public.sabe (
    lenguaje character varying NOT NULL,
    idempleado integer NOT NULL,
    nivel character varying
);

---
-- Definición de Restricciones (Constraints)
---

--
-- Primary Key para empleado
--
ALTER TABLE ONLY public.empleado
    ADD CONSTRAINT empleado_pk PRIMARY KEY (ide);

--
-- Primary Key para lenguaje
--
ALTER TABLE ONLY public.lenguaje
    ADD CONSTRAINT lenguaje_pk PRIMARY KEY (nombre);

--
-- Primary Key compuesta para sabe
--
ALTER TABLE ONLY public.sabe
    ADD CONSTRAINT sabe_pk PRIMARY KEY (lenguaje, idempleado);

--
-- Foreign Key: sabe se relaciona con empleado
--
ALTER TABLE ONLY public.sabe
    ADD CONSTRAINT sabe_empleado_fk FOREIGN KEY (idempleado) REFERENCES public.empleado(ide);

--
-- Foreign Key: sabe se relaciona con lenguaje
--
ALTER TABLE ONLY public.sabe
    ADD CONSTRAINT sabe_lenguaje_fk FOREIGN KEY (lenguaje) REFERENCES public.lenguaje(nombre);


-- *********************************
-- 1. INSERTS para la tabla 'empleado'
-- *********************************

INSERT INTO public.empleado (ide, apenom, mail, telefono, direccion) VALUES
(1, 'Pedro Pascal', 'pedrito.elmas@corpo.com.ar', '11565565200', 'Avenida Secundaria 963'),
(2, 'Ana Torres', 'ana.torres@corporativo.com', '1155500100', 'Avenida Principal 123'),
(3, 'Carlos Gómez', 'carlos.g@empresa.net', '34600998877', 'Calle Falsa 456');


-- *********************************
-- 2. INSERTS para la tabla 'lenguaje'
-- *********************************

INSERT INTO public.lenguaje (nombre) VALUES
('Python'),
('Java'),
('SQL'); -- Agrego SQL para usarlo en la relación

-- *********************************
-- 3. INSERTS para la tabla 'sabe' (Relación)
-- *********************************

-- Establece la relación:
-- Empleado 1 (Gustavo Contardi) sabe Python en nivel Avanzado.
-- Empleado 2 (Ana Torres) sabe Java en nivel Intermedio.
-- Empleado 3 (Carlos Gómez) sabe SQL en nivel Básico.

INSERT INTO public.sabe (lenguaje, idempleado, nivel) VALUES
('Python', 1, 'Avanzado'),
('Java', 2, 'Intermedio'),
('SQL', 3, 'Básico');
