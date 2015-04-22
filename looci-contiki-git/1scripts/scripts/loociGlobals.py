import os

class Looci(object):
    loociEnv = str(os.getenv('LOOCI'))
    lc_contiki_Env = loociEnv + '/lnk/lc_contiki'
    contiki_env = loociEnv + '/lnk/lc_contiki_os'
