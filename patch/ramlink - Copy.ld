ENTRY(xpatch_init)

MEMORY
{
/*
 originally 
  24kB for program
  64kB for variables
 new:
  56kB for program
  64kB for variables 
 next:
  72kB for program!
*/

      SRAM (rwx) : ORIGIN = 0x00010000, LENGTH = 0x0000C000 /* 56kB in RAM1 */
      SRAM2 (rwx) : ORIGIN = 0x2001C000, LENGTH = 0x00002000 /* first 8kB in SRAM2 */
      CCMSRAM (rw) : ORIGIN = 0x10000000, LENGTH = 0x0000E000 /* CCM - ccmramend */
}

SECTIONS {
    .text : ALIGN(16) SUBALIGN(16)
    {
        * (.boot);
        * (.text);
        * (.init_array);
        etext = .;
    } > SRAM
    
    .rodata : ALIGN(4) SUBALIGN(4) 
    {
        * (.rodata);
        * (.rodata.*);
    } > SRAM

    .sram2 (NOLOAD): ALIGN(4) SUBALIGN(4) 
    {
        * (.sram2);
        * (.sram2.*);
    } > SRAM2


    .data : ALIGN(4) SUBALIGN(4)
    {
        sdata = .;
        * (.data);
        edata = .;
    } > CCMSRAM
    
    .bss : ALIGN(4) SUBALIGN(4)
    {
        sbss = .;
        * (.bss);
        ebss = .;
    } > CCMSRAM
    


 /*   
    .ARM.exidx : 
    {
       __exidx_start = .;
       *(.ARM.exidx* .gnu.linkonce.armexidx.*)
       __exidx_end = .;
    } >CCMSRAM
    _etext = .;
*/
}