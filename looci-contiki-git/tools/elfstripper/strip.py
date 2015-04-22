import sys
from elf.structs import ELFStructs
from elf.enums import *
from collections import defaultdict

class Stripper(object):
    
    def __init__(self, stream,outstream):
        self.stream = stream
        self.outstream = outstream
        self._identify_file()
        self.structs = ELFStructs(little_endian=self.little_endian,
                                  elfclass=self.elfclass)
        
        self.header = self._parse_elf_header()
        #print(self.header)
        
        if self.header['e_machine'] != 'EM_AVR':
            raise Exception("NOT AVR ELF FILE!")
        
        self.target_structs = ELFStructs(little_endian=self.little_endian,
                                         elfclass=16)
        
        sectheaders=self._get_sectheaders()
        ordered_sections=self._get_ordered_sections(sectheaders)
        
        sect_sizes = self._calculate_sizes(ordered_sections)
        offsets = self._calculate_offsets(sect_sizes)

                
        #save/convert sym/rela/text/data sections
        saved_sects = list()
        for sect in ordered_sections:
            content = b''
            if sect != "ELF_HEADER" and sect != "SECT_HEADERS":
                if sect['sh_type'] == "SHT_SYMTAB":
                    for i in range(0,sect['sh_size'] // sect['sh_entsize']):
                        offset = sect['sh_offset'] + i*sect['sh_entsize']
                        cont = self._struct_parse(self.structs.Elf_Sym,self.stream,stream_pos=offset)
                        content = content + self.target_structs.Elf_Sym.build(cont)
                elif sect['sh_type'] == "SHT_RELA":
                    for i in range(0,sect['sh_size'] // sect['sh_entsize']):
                        offset = sect['sh_offset'] + i*sect['sh_entsize']
                        cont = self._struct_parse(self.structs.Elf_Rela,self.stream,stream_pos=offset)
                        content = content + self.target_structs.Elf_Rela.build(cont)
                elif sect['sh_size'] != 0:
                    self.stream.seek(sect['sh_offset'])
                    content = self.stream.read(sect['sh_size'])
            saved_sects.append(content)
        #print(saved_sects)
        
        #adjust offsets/flags ELF header
        self.header['e_ehsize']=self.target_structs.Elf_Ehdr.sizeof()
        self.header['e_shentsize']=self.target_structs.Elf_Shdr.sizeof()
        self.header['e_shoff']=offsets[ordered_sections.index("SECT_HEADERS")]
        self.header.e_ident['EI_CLASS']='ELFCLASS16'
        
        #adjust offsets/flags section headers
        for sect in ordered_sections:
            if sect != "ELF_HEADER" and sect != "SECT_HEADERS":
                # TEMP FIX!!!!
                sect['sh_addr'] = 0
                sect['sh_offset'] = offsets[ordered_sections.index(sect)]
                sect['sh_size'] = sect_sizes[ordered_sections.index(sect)]
                if sect['sh_type'] == "SHT_RELA" :
                    sect['sh_entsize'] = self.target_structs.Elf_Rela.sizeof()
                elif sect['sh_type'] == "SHT_SYMTAB":
                    sect['sh_entsize'] = self.target_structs.Elf_Sym.sizeof()
        
        
        
        #rebuild
        for sect in ordered_sections:
            if sect == "ELF_HEADER":
                self._struct_write(self.target_structs.Elf_Ehdr,self.header, self.outstream)
            elif sect == "SECT_HEADERS":
                for s_header in sectheaders:
                    self._struct_write(self.target_structs.Elf_Shdr,s_header,self.outstream)
            else:
                self.outstream.write(saved_sects[ordered_sections.index(sect)])
    
    
    def _identify_file(self):
        """ Verify the ELF file and identify its class and endianness.
            """
        # Note: this code reads the stream directly, without using ELFStructs,
        # since we don't yet know its exact format. ELF was designed to be
        # read like this - its e_ident field is word-size and endian agnostic.
        #
        self.stream.seek(0)
        magic = self.stream.read(4)
        if magic != b'\x7fELF':
            raise Exception('Magic number does not match')
        
        ei_class = self.stream.read(1)
        if ei_class == b'\x01':
            self.elfclass = 32
        elif ei_class == b'\x02':
            self.elfclass = 64
        elif ei_class == b'\x03':
            self.elfclass = 16
        else:
            raise Exception('Invalid EI_CLASS %s' % repr(ei_class))
        
        ei_data = self.stream.read(1)
        if ei_data == b'\x01':
            self.little_endian = True
        elif ei_data == b'\x02':
            self.little_endian = False
        else:
            raise Exception('Invalid EI_DATA %s' % repr(ei_data))
    
    def _get_sectheaders(self):
        sectheaders=list()
       	for i in range(0,self.header['e_shnum']):
            offset = self.header['e_shoff'] + i * self.header['e_shentsize']
            sect = self._parse_section_header(offset)
            sectheaders.append(sect)
        
        return sectheaders
    
    def _get_ordered_sections(self,sectheaders):
        ordered_sects=list()
        ordered_sects.append("ELF_HEADER")
        ordermap=defaultdict(list)
        for sect in sectheaders:
            if sect['sh_type'] != "SHT_NULL":
                ordermap[sect['sh_offset']].append(sect)
        ordermap[self.header['e_shoff']].append("SECT_HEADERS")
        for offset in sorted(ordermap.keys()):
            ordered_sects.extend(ordermap[offset])
        return ordered_sects
    
    def _calculate_sizes(self,ordered_sects):
        sect_sizes=list()
        for sect in ordered_sects:
            if sect == "ELF_HEADER":
                size=self.target_structs.Elf_Ehdr.sizeof()
            elif sect == "SECT_HEADERS":
                size=self.target_structs.Elf_Shdr.sizeof()*self.header['e_shnum']
            elif sect['sh_type'] == "SHT_RELA":
                nr_relocs = sect['sh_size'] // sect['sh_entsize']
                size=nr_relocs*self.target_structs.Elf_Rela.sizeof()
            elif sect['sh_type'] == "SHT_SYMTAB":
                nr_symbs = sect['sh_size'] // sect['sh_entsize']
                size=nr_symbs*self.target_structs.Elf_Sym.sizeof()
            else:
                size=sect['sh_size']
            sect_sizes.append(size)
        return sect_sizes
    
    def _calculate_offsets(self,sizes):
        offsets = list()
        offsets.append(0)
        total = 0
        for size in sizes:
            total = total + size
            offsets.append(total)
        offsets.pop() #last one is useless
        return offsets
    
    def _parse_elf_header(self):
        return self._struct_parse(self.structs.Elf_Ehdr, self.stream, stream_pos=0)
    
    def _parse_section_header(self,offset):
        return self._struct_parse(self.structs.Elf_Shdr, self.stream, offset)
    
    def _struct_parse(self,struct, stream, stream_pos=None):
        try:
            if stream_pos is not None:
                stream.seek(stream_pos)
            return struct.parse_stream(stream)
        except Exception as e:
            raise Exception(e.message)
    
    def _struct_write(self,struct,container, stream, stream_pos=None):
        try:
            if stream_pos is not None:
                stream.seek(stream_pos)
            struct.build_stream(container,stream)
        except Exception as e:
            raise Exception(e.message)


def process_file(infile,outfile):
    print('Processing file:', infile)
    
    f=open(infile,'rb')
    out=open(outfile,'w+b')
    stripper = Stripper(f,out)

if __name__ == '__main__':
    infile = sys.argv[1]
    outfile = sys.argv[2]
    process_file(infile,outfile)
