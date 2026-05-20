import secrets

key = secrets.token_hex(16)   # 32 chars
iv  = secrets.token_hex(8)    # 16 chars

print("AES_SECRET_KEY=" + key)
print("AES_IV=" + iv)

# !!!!!!!! used only once to generate secret keys
# (pasted in .env dakchy rah khdam adrari chofo l guide dial Crypto bach t3rfo kif tst3mlo dkchy)
# how generated :
# C:\Users\soufi\Documents\ENSA-GI-1ST\S4\JEE\Control-2\e-banking> python.exe .\encryption-keys-generator.py
# AES_SECRET_KEY=fdb501b55b32d91cbd5957beb846dea8
# AES_IV=677722ca8f2f3a9c
# w drna lehom copier coller