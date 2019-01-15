#! /usr/bin/python3

import hashlib

#print("hello world")

def grabFile(fileName):
  textFile = ""

  with open(fileName) as file:
    textFile = file.read()

  return textFile

def tryString(attempted_string, salt, correct_hash):

  m = hashlib.md5()

  m.update(attempted_string)

  print(m.hexdigest())

  return m.hexdigest() == correct_hash




def crack(salt, correct_hash):
  return "temp_crack_retval"
  
def test_tryString():

  print(tryString("test", "001", "4bc5822a79417bca40ef56cb86a2bf7e"))

def test_iters():

  #try printing all chars from a-z
  #for i in range(ord('a'), ord('z') + 1):
  #  print(chr(i))
  #  print("\n")

  #now, try printing all chars from a-zzz
  for i1 in range(ord('a'), ord('z') + 1):
    
    s1 = chr(i1)
    print(s1)

    for i2 in range(ord('a'), ord('z') + 1):

      s2 = s1 + chr(i2)
      print(s2)

      for i3 in range(ord('a'), ord('z') + 1):

        s3 = s2 + chr(i3)
        print(s3)

def main():

  test_tryString()

  salt = "hfT7jp2q"
  correct_hash = "JU0X9xRQyTWTWY59e3Iqj1"

  pw = crack(salt, correct_hash)

  with open("result.txt", 'w') as file:

    file.write(pw)




if __name__ == "__main__":
  main()

