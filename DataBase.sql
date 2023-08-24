PGDMP     (    #                {           KartyBankowe    15.3    15.3     �           0    0    ENCODING    ENCODING        SET client_encoding = 'UTF8';
                      false                        0    0 
   STDSTRINGS 
   STDSTRINGS     (   SET standard_conforming_strings = 'on';
                      false                       0    0 
   SEARCHPATH 
   SEARCHPATH     8   SELECT pg_catalog.set_config('search_path', '', false);
                      false                       1262    16542    KartyBankowe    DATABASE     �   CREATE DATABASE "KartyBankowe" WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'Polish_Poland.1250';
    DROP DATABASE "KartyBankowe";
                postgres    false            �            1259    16565    informacje_o_klientach    TABLE     �   CREATE TABLE public.informacje_o_klientach (
    numer_konta character varying(26) NOT NULL,
    saldo numeric(10,2),
    wlasciciel character varying(256),
    data_waznosci_karty date,
    cvv character varying(3)
);
 *   DROP TABLE public.informacje_o_klientach;
       public         heap    postgres    false            �            1259    16570    karty_bankowe    TABLE     �   CREATE TABLE public.karty_bankowe (
    numer_karty character varying(16) NOT NULL,
    pin character varying(4),
    numer_konta character varying(26)
);
 !   DROP TABLE public.karty_bankowe;
       public         heap    postgres    false            �          0    16565    informacje_o_klientach 
   TABLE DATA           j   COPY public.informacje_o_klientach (numer_konta, saldo, wlasciciel, data_waznosci_karty, cvv) FROM stdin;
    public          postgres    false    214   �       �          0    16570    karty_bankowe 
   TABLE DATA           F   COPY public.karty_bankowe (numer_karty, pin, numer_konta) FROM stdin;
    public          postgres    false    215   6       i           2606    16569 2   informacje_o_klientach informacje_o_klientach_pkey 
   CONSTRAINT     y   ALTER TABLE ONLY public.informacje_o_klientach
    ADD CONSTRAINT informacje_o_klientach_pkey PRIMARY KEY (numer_konta);
 \   ALTER TABLE ONLY public.informacje_o_klientach DROP CONSTRAINT informacje_o_klientach_pkey;
       public            postgres    false    214            k           2606    16574     karty_bankowe karty_bankowe_pkey 
   CONSTRAINT     g   ALTER TABLE ONLY public.karty_bankowe
    ADD CONSTRAINT karty_bankowe_pkey PRIMARY KEY (numer_karty);
 J   ALTER TABLE ONLY public.karty_bankowe DROP CONSTRAINT karty_bankowe_pkey;
       public            postgres    false    215            l           2606    16575 ,   karty_bankowe karty_bankowe_numer_konta_fkey    FK CONSTRAINT     �   ALTER TABLE ONLY public.karty_bankowe
    ADD CONSTRAINT karty_bankowe_numer_konta_fkey FOREIGN KEY (numer_konta) REFERENCES public.informacje_o_klientach(numer_konta);
 V   ALTER TABLE ONLY public.karty_bankowe DROP CONSTRAINT karty_bankowe_numer_konta_fkey;
       public          postgres    false    3177    214    215            �   l   x�=�1
�@D�Z:�/�F#���t�@N�fKc�)R��������f
8U] �~x\Dfz�1�������T�h1���j��۷�ޑ2��2F���Nm����?33:s�      �   I   x�=˱�0�����M/��ė��/���r2i�uM��w�P����վ��Xg?��^����E     