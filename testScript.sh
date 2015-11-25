BENCHMARKPATH=../../benchmark/benchmark_
echo BENCHMARKPATH"$1"
cd ./bin
./computePatchOpt BENCHMARKPATH"$1"/source BENCHMARKPATH"1"/target > patchResult.txt
./applyPatch patchResult.txt BENCHMARKPATH"$1"/source > applyResult.txt
diff BENCHMARKPATH"$1"/target ./applyResult.txt
rm ./patchResult.txt
rm ./applyResult.txt
cd ..
